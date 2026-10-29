import { BrowserRouter, Link, NavLink, Route, Routes, useNavigate } from 'react-router-dom';
import { Button, Container, Nav, Navbar } from 'react-bootstrap';
import { AuthProvider, RequireRole, useAuth } from './LuanLHCE191099_Auth.jsx';
import LoginPage from './LuanLHCE191099_LoginPage.jsx';
import PublicRooms from './LuanLHCE191099_PublicRooms.jsx';
import CustomerPage, { customerFields } from './LuanLHCE191099_CustomerPage.jsx';
import RoomPage from './LuanLHCE191099_RoomPage.jsx';
import BookingPage, { bookingColumns } from './LuanLHCE191099_BookingPage.jsx';
import CrudPage from './LuanLHCE191099_CrudPage.jsx';
import ProfilePage from './LuanLHCE191099_ProfilePage.jsx';
import './LuanLHCE191099_styles.css';
const profileFields = customerFields.map((field) =>
  field.name === 'password' ? { ...field, required: false, label: 'New password' } : field,
);
function Shell() {
  const { user, logout } = useAuth();
  const navigate = useNavigate();
  return (
    <>
      <Navbar bg="dark" variant="dark" expand="lg" className="site-nav">
        <Container>
          <Navbar.Brand as={Link} to="/" className="site-brand">
            FU / MINI HOTEL
          </Navbar.Brand>
          <Navbar.Toggle aria-controls="hotel-menu" />
          <Navbar.Collapse id="hotel-menu">
            <Nav className="me-auto">
              <Nav.Link as={NavLink} to="/" end>
                Rooms
              </Nav.Link>
              {user?.role === 'STAFF' && (
                <>
                  <Nav.Link as={NavLink} to="/manage/customers">
                    Customers
                  </Nav.Link>
                  <Nav.Link as={NavLink} to="/manage/rooms">
                    Manage rooms
                  </Nav.Link>
                  <Nav.Link as={NavLink} to="/manage/bookings">
                    Reservations
                  </Nav.Link>
                </>
              )}
              {user?.role === 'CUSTOMER' && (
                <>
                  <Nav.Link as={NavLink} to="/history">
                    My bookings
                  </Nav.Link>
                  <Nav.Link as={NavLink} to="/profile">
                    My profile
                  </Nav.Link>
                </>
              )}
            </Nav>
            {user ? (
              <div className="d-flex gap-3 align-items-center text-white">
                <small>{user.name}</small>
                <Button
                  variant="outline-light"
                  size="sm"
                  onClick={() => {
                    logout();
                    navigate('/');
                  }}
                >
                  Sign out
                </Button>
              </div>
            ) : (
              <Button as={Link} to="/login" variant="outline-light" size="sm">
                Sign in / Register
              </Button>
            )}
          </Navbar.Collapse>
        </Container>
      </Navbar>
      <Container className="py-4">
        <Routes>
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
          <Route element={<RequireRole roles={['STAFF']} />}>
            <Route path="/manage/customers" element={<CustomerPage />} />
            <Route path="/manage/rooms" element={<RoomPage />} />
            <Route path="/manage/bookings" element={<BookingPage />} />
          </Route>
          <Route element={<RequireRole roles={['CUSTOMER']} />}>
            <Route
              path="/history"
              element={
                <CrudPage
                  title="My booking history"
                  endpoint="/my/bookings"
                  columns={bookingColumns}
                  readOnly
                />
              }
            />
            <Route
              path="/profile"
              element={<ProfilePage endpoint="/my/profile" fields={profileFields} />}
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
        </Routes>
      </Container>
      <footer className="container py-4 text-secondary small">
        FUMiniHotelSystem · Assignment 03 · LuanLH / CE191099
      </footer>
    </>
  );
}
export default function App() {
  return (
    <BrowserRouter>
      <AuthProvider>
        <Shell />
      </AuthProvider>
    </BrowserRouter>
  );
}
