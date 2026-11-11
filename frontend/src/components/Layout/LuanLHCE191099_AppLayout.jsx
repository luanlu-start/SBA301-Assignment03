import { Container } from 'react-bootstrap';
import { Outlet } from 'react-router-dom';
import Header from './LuanLHCE191099_Header.jsx';

export default function AppLayout() {
  return (
    <>
      <Header />
      <Container className="py-4">
        <Outlet />
      </Container>
      <footer className="container py-4 text-secondary small">
        FUMiniHotelSystem · Assignment 03 · LuanLH / CE191099
      </footer>
    </>
  );
}
