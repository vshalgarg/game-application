    package com.codemonks.ludo_engine.service.Impl;
    import com.codemonks.ludo_engine.model.BoardLayout;
    import com.codemonks.ludo_engine.model.Grid;
    import com.codemonks.ludo_engine.service.BoardLoader;
    import com.codemonks.ludo_engine.service.BoardService;
    import lombok.RequiredArgsConstructor;
    import org.springframework.stereotype.Service;

    import java.util.List;
    import java.util.Map;
    import java.util.Set;
    import java.util.stream.Collectors;

    @Service
    @RequiredArgsConstructor
    public class BoardServiceImpl implements BoardService {

        private final BoardLoader boardLoader;

        @Override
        public BoardLayout getBoard() {
            return boardLoader.getBoardLayout();
        }

        @Override
        public Grid getCell(Integer cellId) {
            return boardLoader
                    .getBoardLayout()
                    .getGrid()
                    .get(cellId);
        }

    @Override
    public List<Integer> getPath(Integer colorIndex) {

        return boardLoader
                .getBoardLayout()
                .getPaths()
                .get(colorIndex);
    }

        @Override
        public List<String> getColors() {
            return boardLoader
                    .getBoardLayout()
                    .getMetadata()
                    .getColors();
        }

        @Override
        public Set<Integer> getSafeCells() {

            return boardLoader
                    .getBoardLayout()
                    .getGrid()
                    .stream()
                    .filter(cell ->
                            "SAFE".equalsIgnoreCase(cell.getType())
                                    || "START".equalsIgnoreCase(cell.getType()))
                    .map(Grid::getId)
                    .collect(Collectors.toSet());
        }

        @Override
        public Integer getBaseSlotId(Integer colorIndex, Integer baseSlot) {
            Map<Integer, List<Integer>> baseCells = boardLoader.getBoardLayout().getBaseCells();
            if (baseCells == null) return null;
            List<Integer> slots = baseCells.get(colorIndex);
            if (slots == null || baseSlot == null || baseSlot >= slots.size()) return null;
            return slots.get(baseSlot);
        }

}