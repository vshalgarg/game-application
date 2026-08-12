import { BrowserRouter } from "react-router-dom";
import AppRoutes from "./routes/AppRoutes";
import { SnackbarProvider } from "./context/SnackbarContext";
import { AuthProvider } from "./context/AuthContext";

function App() {
  return (
    <BrowserRouter>
      <SnackbarProvider>
        <AuthProvider>
          <AppRoutes />
        </AuthProvider>
      </SnackbarProvider>
    </BrowserRouter>
  );
}

export default App;