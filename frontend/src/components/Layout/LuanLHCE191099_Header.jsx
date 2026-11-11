import { Link, NavLink, useNavigate } from 'react-router-dom';
import { Button, Container, Nav, Navbar } from 'react-bootstrap';
import { useAuth } from '../../context/LuanLHCE191099_AuthContext.jsx';

export default function Header() {
  const { user, logout } = useAuth();
  const navigate = useNavigate();

  return (
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
  );
}
