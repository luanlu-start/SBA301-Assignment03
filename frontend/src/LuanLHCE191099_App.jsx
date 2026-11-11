import { BrowserRouter, Link, Route, Routes } from 'react-router-dom';
import AppLayout from './components/Layout/LuanLHCE191099_AppLayout.jsx';
import ProtectedRoute from './components/LuanLHCE191099_ProtectedRoute.jsx';
import { AuthProvider } from './context/LuanLHCE191099_AuthContext.jsx';
import BookingPage from './pages/LuanLHCE191099_BookingPage.jsx';
import CustomerPage, { customerFields } from './pages/LuanLHCE191099_CustomerPage.jsx';
import HistoryPage from './pages/LuanLHCE191099_HistoryPage.jsx';
import LoginPage from './pages/LuanLHCE191099_LoginPage.jsx';
import ProfilePage from './pages/LuanLHCE191099_ProfilePage.jsx';
import PublicRooms from './pages/LuanLHCE191099_PublicRooms.jsx';
import RoomPage from './pages/LuanLHCE191099_RoomPage.jsx';
import './LuanLHCE191099_styles.css';

const profileFields = customerFields.map((field) =>
  field.name === 'password' ? { ...field, required: false, label: 'New password' } : field,
);

export default function App() {
  return (
    <BrowserRouter>
      <AuthProvider>
        <Routes>
          <Route element={<AppLayout />}>
            <Route path="/" element={<PublicRooms />} />
            <Route
              path="/login"
              element={
                <LoginPage
                  registerFields={customerFields}
                  destinations={{ STAFF: '/manage/bookings', CUSTOMER: '/' }}
                />
              }
            />
            <Route element={<ProtectedRoute roles={['STAFF']} />}>
              <Route path="/manage/customers" element={<CustomerPage />} />
              <Route path="/manage/rooms" element={<RoomPage />} />
              <Route path="/manage/bookings" element={<BookingPage />} />
            </Route>
            <Route element={<ProtectedRoute roles={['CUSTOMER']} />}>
              <Route path="/history" element={<HistoryPage />} />
              <Route
                path="/profile"
                element={<ProfilePage fields={profileFields} />}
              />
            </Route>
            <Route
              path="*"
              element={
                <p>
                  Page not found. <Link to="/">Return to rooms</Link>
                </p>
              }
            />
          </Route>
        </Routes>
      </AuthProvider>
    </BrowserRouter>
  );
}
