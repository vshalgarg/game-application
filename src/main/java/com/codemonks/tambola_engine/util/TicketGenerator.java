package com.codemonks.tambola_engine.util;


import com.codemonks.tambola_engine.domain.ticket.TambolaTicket;
import com.codemonks.tambola_engine.domain.ticket.TicketRow;
import com.codemonks.tambola_engine.exception.TicketPersistenceException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicLong;

// Standard Tambola ticket generate karne ki responsibility isi class ki hai.
// Ek ticket = 3 rows x 9 columns, jismein sirf 15 cells filled hote hain
// (har row me exactly 5 numbers), baaki 12 cells blank (null) rehte hain.
//
// @Component - ye ek Spring-managed singleton hai, kyunki isme koi per-room
// state nahi hai (sirf ek shared ticketId counter hai jo poore engine ke
// liye globally unique IDs deta hai).
//
// FUTURE NOTE: Abhi ye sirf GameSetupService se call hota hai (game-start
// ke waqt, har player ke fixed tickets banane ke liye). Lekin isko
// jaanbujh kar "per-call = ek ticket banao aur unique ID do" ke tarah
// design kiya hai (na ki "poore room ke saare tickets ek saath banao"),
// taaki AAGE jaake jab "coin se ticket khareedo" feature aaye (mid-game,
// dynamic ticket count per player), tab bhi yehi method reuse ho sake -
// ek naya BuyTicketService bas isi generateTicket() ko call karega,
// koi restructuring nahi karni padegi.
@Component
public class TicketGenerator {

    // Tambola ticket ka standard shape - kabhi nahi badalta, isliye constants.
    private static final int TOTAL_ROWS = 3;
    private static final int TOTAL_COLUMNS = 9;
    private static final int NUMBERS_PER_ROW = 5;
    private static final int TOTAL_FILLED_CELLS = TOTAL_ROWS * NUMBERS_PER_ROW; // 15
    private static final int MAX_NUMBERS_PER_COLUMN = 3; // 3 rows hain, ek column me max 3 hi aa sakte

    // Har naye ticket ko globally-unique ID dene ke liye shared counter.
    // AtomicLong isliye kyunki multiple threads (different rooms ke setup
    // requests ek saath aa sakte hain) se safely increment hona chahiye.
    // NOTE: Ye in-memory hai - JVM restart hone par 1 se reset ho jaayega.
    // Runtime-only identifiers ke liye abhi ye theek hai (jaisa NumberGenerator
    // aur baaki engine bhi purely in-memory hai); agar future me IDs ko
    // restart ke paar bhi persist/unique rehna zaroori ho, isko Supabase
    // se DB-generated ID lene wale approach me badalna hoga.
    private final AtomicLong ticketIdSequence = new AtomicLong(1);

    /**
     * Ek naya, valid Tambola ticket generate karta hai ek player ke liye.
     * Har call ek naya unique ticketId deta hai - isliye ye method
     * setup ke waqt (N baar loop me) aur future ticket-purchase ke waqt
     * (ek-ek baar), dono jagah reusable hai.
     */
    public TambolaTicket generateTicket(Long playerId) {

        ThreadLocalRandom random = ThreadLocalRandom.current();

        // Step 1: Decide karo har column me kitne numbers honge (1 se 3 ke beech),
        // taaki total 15 ho jaaye (3 rows x 5 numbers).
        int[] columnCounts = generateColumnCounts(random);

        // Step 2: Decide karo har column ke numbers KIS ROW me jaayenge,
        // taaki har row ka total exactly 5 ho (constraint satisfy ho).
        List<List<Integer>> rowsPerColumn = assignRowsToColumns(columnCounts, random);

        // Step 3: Ab actual numbers fill karo - 3x9 ka empty grid banao,
        // phir har column ke liye us column-range se random numbers
        // choose karke sahi rows me daalo.
        Integer[][] grid = new Integer[TOTAL_ROWS][TOTAL_COLUMNS];

        for (int column = 0; column < TOTAL_COLUMNS; column++) {
            int countForThisColumn = columnCounts[column];
            if (countForThisColumn == 0) {
                continue; // is column me koi number nahi aana - saara blank rahega
            }

            // Us column ke valid number-range se (jaise column 0 = 1-9,
            // column 8 = 80-90) utne hi random numbers nikalo jitni zaroorat hai.
            List<Integer> pickedNumbers = pickRandomNumbersForColumn(column, countForThisColumn, random);

            // Numbers ko ascending order me sort karo - taaki upar wali row
            // me chhota number aaye, niche wali me bada (ye Tambola ka visual
            // convention hai, real tickets me bhi top-se-bottom ascending hote hain).
            Collections.sort(pickedNumbers);

            // In numbers ko unhi rows me daalo jo Step 2 me decide hui thi.
            List<Integer> rowsForThisColumn = rowsPerColumn.get(column);
            for (int i = 0; i < countForThisColumn; i++) {
                int targetRow = rowsForThisColumn.get(i);
                grid[targetRow][column] = pickedNumbers.get(i);
            }
        }

        // Grid ke har row ko TicketRow object me convert karo.
        List<TicketRow> ticketRows = new ArrayList<>();
        for (int row = 0; row < TOTAL_ROWS; row++) {
            ticketRows.add(new TicketRow(Arrays.asList(grid[row])));
        }

        // Naye ticket ko unique ID do (counter se), aur poora object return karo.
        Long ticketId = ticketIdSequence.getAndIncrement();
        return new TambolaTicket(ticketId, playerId, ticketRows);
    }

    // Har column me kitne numbers honge, ye decide karta hai.
    // Rule: 9 columns, total 15 numbers, har column me minimum 1, maximum 3.
    private int[] generateColumnCounts(ThreadLocalRandom random) {
        int[] counts = new int[TOTAL_COLUMNS];

        // Sabse pehle har column ko minimum 1 number do (9 columns x 1 = 9).
        Arrays.fill(counts, 1);

        // Ab bache hue 6 numbers (15 - 9 = 6) ko randomly kisi bhi column
        // me daal do, jab tak wo column already 3 (max) tak na pahunch jaaye.
        int remaining = TOTAL_FILLED_CELLS - TOTAL_COLUMNS;
        while (remaining > 0) {
            int randomColumn = random.nextInt(TOTAL_COLUMNS);
            if (counts[randomColumn] < MAX_NUMBERS_PER_COLUMN) {
                counts[randomColumn]++;
                remaining--;
            }
            // agar wo column already full (3) hai, to loop bas dobara try karega
            // (chhota sample-space hai, isliye ye jaldi hi converge ho jaata hai)
        }

        return counts;
    }

    // Har column ke numbers ko specific rows assign karta hai, taaki har
    // row ka total exactly 5 ho jaaye. Ye ek constraint-satisfaction problem
    // hai, isliye randomized-retry approach use kiya hai - chhota sa search
    // space hone ki wajah se ye almost hamesha pehle hi ya doosre attempt
    // me successfully solve ho jaata hai.
    private List<List<Integer>> assignRowsToColumns(int[] columnCounts, ThreadLocalRandom random) {
        int maxAttempts = 200; // safety limit - practically kabhi itne attempts nahi lagenge

        for (int attempt = 0; attempt < maxAttempts; attempt++) {
            // Har row me abhi kitni jagah (numbers) baaki hai - shuru me 5,5,5.
            int[] rowRemainingCapacity = {NUMBERS_PER_ROW, NUMBERS_PER_ROW, NUMBERS_PER_ROW};

            List<List<Integer>> assignment = new ArrayList<>();
            for (int c = 0; c < TOTAL_COLUMNS; c++) {
                assignment.add(new ArrayList<>());
            }

            // Columns ko random order me process karenge, taaki har baar
            // ticket ka layout alag-alag bane (variety ke liye).
            List<Integer> columnOrder = new ArrayList<>();
            for (int c = 0; c < TOTAL_COLUMNS; c++) columnOrder.add(c);
            Collections.shuffle(columnOrder, random);

            boolean attemptFailed = false;

            for (int column : columnOrder) {
                int needed = columnCounts[column];

                // Konsi rows me abhi bhi jagah bachi hai (capacity > 0)?
                List<Integer> availableRows = new ArrayList<>();
                for (int row = 0; row < TOTAL_ROWS; row++) {
                    if (rowRemainingCapacity[row] > 0) {
                        availableRows.add(row);
                    }
                }

                // Agar itni rows hi available nahi hain jitni is column ko
                // chahiye (jaise column ko 3 rows chahiye but sirf 2 me jagah
                // bachi hai), ye attempt fail - poora dobara try karo.
                if (availableRows.size() < needed) {
                    attemptFailed = true;
                    break;
                }

                // Available rows me se random `needed` rows choose karo.
                Collections.shuffle(availableRows, random);
                List<Integer> chosenRows = availableRows.subList(0, needed);

                for (int row : chosenRows) {
                    assignment.get(column).add(row);
                    rowRemainingCapacity[row]--;
                }
            }

            // Agar sab columns successfully assign ho gaye AUR har row ki
            // capacity bilkul 0 bach gayi (matlab har row me exactly 5 aa
            // gaye), to ye valid layout hai - return kar do.
            if (!attemptFailed
                    && rowRemainingCapacity[0] == 0
                    && rowRemainingCapacity[1] == 0
                    && rowRemainingCapacity[2] == 0) {
                return assignment;
            }
            // Warna is attempt ko chhod ke naya attempt try karo.
        }

        // Itna chhota constraint-problem hai ki ye line practically kabhi
        // nahi chalni chahiye - lekin agar chale, saaf error do bajaye
        // silently corrupt ticket return karne ke. IllegalStateException
        // ki jagah dedicated exception - taaki GlobalExceptionHandler ka
        // "room not found" handler ise galti se catch na kar le.
        throw new TicketPersistenceException(
                "Ticket layout generate nahi ho paya after " + maxAttempts + " attempts"
        );

    }

    // Diye gaye column ke valid number-range se `count` unique random
    // numbers choose karta hai.
    private List<Integer> pickRandomNumbersForColumn(int column, int count, ThreadLocalRandom random) {
        int rangeStart;
        int rangeEnd;

        // Standard Tambola column-ranges:
        // Column 0 -> 1-9, Column 1-7 -> 10-19...70-79, Column 8 -> 80-90.
        if (column == 0) {
            rangeStart = 1;
            rangeEnd = 9;
        } else if (column == TOTAL_COLUMNS - 1) {
            rangeStart = 80;
            rangeEnd = 90;
        } else {
            rangeStart = column * 10;
            rangeEnd = column * 10 + 9;
        }

        // Us range ke saare possible numbers ek list me daalo, shuffle karo,
        // aur pehle `count` utha lo - isse guaranteed unique numbers milte hain
        // bina duplicate-check ki extra logic likhe.
        List<Integer> allNumbersInRange = new ArrayList<>();
        for (int n = rangeStart; n <= rangeEnd; n++) {
            allNumbersInRange.add(n);
        }
        Collections.shuffle(allNumbersInRange, random);

        return new ArrayList<>(allNumbersInRange.subList(0, count));
    }
}