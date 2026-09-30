import { FaPlane, FaTicketAlt, FaDice, FaTimes } from "react-icons/fa";

export const popularGames = [
  {
    id: "tic-tac-toe",
    title: "Tic Tac Toe",
    genre: "Puzzle",
    path: "/tic-tac-toe",
    icon: FaTimes,
    accent: "cyan",
  },
  {
    id: "ludo",
    title: "Ludo",
    genre: "Board",
    path: "/createjoin-room",
    icon: FaDice,
    accent: "purple",
  },
  {
    id: "chief-drop",
    title: "Chief Drop",
    genre: "Fall",
    path: "/chief-drop",
    icon: FaPlane,
    accent: "cyan",
  },
  {
    id: "tambola",
    title: "Tambola",
    genre: "Numbers",
    path: "/tambola-mode",
    icon: FaTicketAlt,
    accent: "purple",
  },
];
