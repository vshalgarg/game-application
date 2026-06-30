import AppRoutes from "./routes/AppRoutes";
import { SnackbarProvider } from "./context/SnackbarContext";

function App() {
  return (
    <SnackbarProvider>
      <AppRoutes />
    </SnackbarProvider>
  );
}

export default App;