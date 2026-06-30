import { Box, Button, AppBar, Toolbar, Typography, Container, CssBaseline } from "@mui/material"
import { useContext, useEffect, useState } from "react"
import { AuthContext } from "react-oauth2-code-pkce"
import { useDispatch } from "react-redux";
import { BrowserRouter, Routes, Route, Navigate } from "react-router"
import { setCredentials } from "./store/authSlice";
import ActivityForm from "./components/ActivityForm"
import ActivityList from "./components/ActivityList"
import ActivityDetail from "./components/ActivityDetail";

const ActivitiesPage = () => {
  return (
    <Box component="section" sx={{ display: 'flex', flexDirection: 'column', gap: 3 }}>
      <ActivityForm onActivitiesAdded={() => window.location.reload()}/>
      <ActivityList/>
    </Box>
  )
}

function App() {
  const { token, tokenData, logIn, logOut } = useContext(AuthContext);
  const dispatch = useDispatch();
  const [authReady, setAuthReady] = useState(false);

  useEffect(() => {
    if (token) {
      dispatch(setCredentials({ token, user: tokenData }));
      setAuthReady(true);
    }
  }, [token, tokenData, dispatch]);

  return (
    <BrowserRouter>
      <CssBaseline />
      {!token ? (
        /* Simple, clean landing panel with your exact login call unaltered */
        <Box 
          sx={{ 
            height: '100vh', 
            display: 'flex', 
            flexDirection: 'column',
            justifyContent: 'center', 
            alignItems: 'center',
            backgroundColor: '#f5f5f5',
            gap: 2
          }}
        >
          <Typography variant="h4" component="h1" sx={{ fontWeight: 700, color: '#1a1a1a' }}>
            Welcome to Activity Tracker
          </Typography>
          <Typography variant="body1" sx={{ color: '#666', mb: 2 }}>
            Please log in to manage and view your progress.
          </Typography>
          <Button 
            variant="contained" 
            sx={{ backgroundColor: '#dc004e', '&:hover': { backgroundColor: '#b2003e' } }} 
            onClick={() => {
              logIn();
            }}
          >
            LOGIN
          </Button>
        </Box>
      ) : (
        /* Main Layout Screen */
        <Box sx={{ display: 'flex', flexDirection: 'column', minHeight: '100vh' }}>
          <AppBar position="static" sx={{ backgroundColor: '#fff', color: '#1a1a1a' }} elevation={1}>
            <Toolbar>
              <Typography variant="h6" component="div" sx={{ flexGrow: 1, fontWeight: 700 }}>
                Activity Tracker
              </Typography>
              <Button variant="contained" color="secondary" onClick={logOut}>
                Logout
              </Button>
            </Toolbar>
          </AppBar>

          <Container component="main" maxWidth="md" sx={{ mt: 4, mb: 4 }}>
            <Routes>
              <Route path="/activities" element={<ActivitiesPage/>}/>
              <Route path="/activities/:id" element={<ActivityDetail/>}/>
              <Route path="/" element={token ? <Navigate to="/activities" replace/> : <div>Welcome! Please Login</div>}/>
            </Routes>
          </Container>
        </Box>
      )}
    </BrowserRouter>
  )
}

export default App