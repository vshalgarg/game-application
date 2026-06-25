const layout = Array(15)
  .fill(null)
  .map(() => Array(15).fill("#e5e7eb"));


// RED HOME (Top Left)

for (let row = 0; row < 6; row++) {
  for (let col = 0; col < 6; col++) {
    layout[row][col] = "#ef4444";
  }
}


// GREEN HOME (Top Right)

for (let row = 0; row < 6; row++) {
  for (let col = 9; col < 15; col++) {
    layout[row][col] = "#22c55e";
  }
}


// BLUE HOME (Bottom Left)

for (let row = 9; row < 15; row++) {
  for (let col = 0; col < 6; col++) {
    layout[row][col] = "#3b82f6";
  }
}


// YELLOW HOME (Bottom Right)

for (let row = 9; row < 15; row++) {
  for (let col = 9; col < 15; col++) {
    layout[row][col] = "#facc15";
  }
}

// Vertical Path

for (let row = 0; row < 15; row++) {
  layout[row][6] = "#ffffff";
  layout[row][7] = "#ffffff";
  layout[row][8] = "#ffffff";
}


// Horizontal Path

for (let col = 0; col < 15; col++) {
  layout[6][col] = "#ffffff";
  layout[7][col] = "#ffffff";
  layout[8][col] = "#ffffff"; 
}

// RED HOME LANE

for (let col = 1; col <= 5; col++) {
  layout[7][col] = "#ef4444";
}

// GREEN HOME LANE

for (let col = 9; col <= 13; col++) {
  layout[7][col] = "#facc15";
}

// BLUE HOME LANE

for (let row = 9; row <= 13; row++) {
  layout[row][7] = "#3b82f6";
}

// YELLOW HOME LANE

for (let row = 1; row <= 5; row++) {
  layout[row][7] = "#22c55e";
}

// center 3*3 grid part 
for (let row = 6; row <= 8; row++) {
  for (let col = 6; col <= 8; col++) {
    layout[row][col] = "#ffffff";
  }
}

// for white dots on the home areas  
for (let row = 1; row <= 4; row++) {
  for (let col = 1; col <= 4; col++) {
    layout[row][col] = "#ffffff";
  }
}

for (let row = 1; row <= 4; row++) {
  for (let col = 10; col <= 13; col++) {
    layout[row][col] = "#ffffff";
  }
}

for (let row = 10; row <= 13; row++) {
  for (let col = 1; col <= 4; col++) {
    layout[row][col] = "#ffffff";
  }
}

for (let row = 10; row <= 13; row++) {
  for (let col = 10; col <= 13; col++) {
    layout[row][col] = "#ffffff";
  }
}

// layout[6][1] = "safe";
// layout[1][8] = "safe";
// layout[8][13] = "safe";
// layout[13][6] = "safe";
layout[6][1] = "#ef4444";
layout[1][8] = "#22c55e";
layout[8][13] = "#facc15";
layout[13][6] = "#3b82f6";

// Safe Cells (Stars)

layout[8][2] = "safe";   // Green side
layout[2][6] = "safe";   // Red side
layout[6][12] = "safe";  // Yellow side
layout[12][8] = "safe";  // Blue side

// Entry Arrows

layout[7][0] = "red-arrow";
layout[0][7] = "green-arrow";
layout[14][7] = "blue-arrow";
layout[7][14] = "yellow-arrow";

export const boardLayout = layout;