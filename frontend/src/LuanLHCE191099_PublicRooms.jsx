import { useState } from 'react';
import { Link } from 'react-router-dom';
import { Alert, Badge, Button, Card, Col, Form, Row, Spinner } from 'react-bootstrap';
import api, { errorText, money } from './LuanLHCE191099_api.js';
import { useAuth } from './LuanLHCE191099_Auth.jsx';
import useApiList from './LuanLHCE191099_useApiList.js';
function day(offset) {
  const date = new Date();
  date.setDate(date.getDate() + offset);
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`;
}
export default function PublicRooms() {
  const { user } = useAuth();
  const [search, setSearch] = useState('');
  const [dates, setDates] = useState({ startDate: day(1), endDate: day(2) });
  const [filterDates, setFilterDates] = useState('');
  const rooms = useApiList(`/public/rooms?q=${encodeURIComponent(search)}${filterDates}`);
  const [selectedIds, setSelectedIds] = useState([]);
  const [error, setError] = useState('');
  const [notice, setNotice] = useState('');
  const [busy, setBusy] = useState(false);
  const selected = rooms.data.filter((room) => selectedIds.includes(room.id));
  const nights = Math.max(
    0,
    Math.round((new Date(dates.endDate) - new Date(dates.startDate)) / 86400000),
  );
  const estimate = selected.reduce((sum, room) => sum + room.price * nights, 0);
  function searchAvailable(event) {
    event.preventDefault();
    setError('');
    if (dates.endDate <= dates.startDate) {
      setError('Check-out must be after check-in.');
      return;
    }
    setFilterDates(`&startDate=${dates.startDate}&endDate=${dates.endDate}`);
    setSelectedIds([]);
    rooms.reload();
  }
  async function reserve() {
    setError('');
    setNotice('');
    if (!selected.length) {
      setError('Select at least one room.');
      return;
    }
    if (nights < 1) {
      setError('Check-out must be after check-in.');
      return;
    }
    setBusy(true);
    try {
      const { data } = await api.post('/bookings', {
        ...dates,
        roomIds: selected.map((room) => room.id),
        status: 1,
      });
      setNotice(
        `Reservation #${data.id} confirmed. Total: ${money(data.totalPrice)}. You can review it in My bookings.`,
      );
      setSelectedIds([]);
      rooms.reload();
    } catch (error) {
      setError(errorText(error));
    } finally {
      setBusy(false);
    }
  }
  function toggle(id) {
    setSelectedIds((ids) =>
      ids.includes(id) ? ids.filter((value) => value !== id) : [...ids, id],
    );
  }
  return (
    <>
      <section className="public-intro">
        <p className="text-uppercase small fw-bold">FUMiniHotelSystem</p>
        <h1>A comfortable place for your next stay.</h1>
        <p className="mt-3 mb-0">
          Explore room types, check dates and reserve one or more rooms for your visit.
        </p>
      </section>
      {notice && <Alert variant="success">{notice}</Alert>}
      {(error || rooms.error) && <Alert variant="danger">{error || rooms.error}</Alert>}
      <Row className="g-4">
        <Col lg={8}>
          <Form.Label htmlFor="room-search">Find a room</Form.Label>
          <Form.Control
            id="room-search"
            className="mb-4"
            value={search}
            onChange={(event) => {
              setSearch(event.target.value);
              setSelectedIds([]);
            }}
            placeholder="Search room number, type or description..."
          />
          {rooms.loading ? (
            <Spinner animation="border" />
          ) : (
            <Row className="g-3">
              {rooms.data.length ? (
                rooms.data.map((room) => (
                  <Col md={6} key={room.id}>
                    <Card className="h-100">
                      <div className="room-number">Room {room.number}</div>
                      <Card.Body>
                        <Badge bg="light" text="dark" className="mb-2">
                          {room.roomTypeName}
                        </Badge>
                        <Card.Title>
                          {money(room.price)}{' '}
                          <small className="fw-normal text-secondary">/ night</small>
                        </Card.Title>
                        <p className="text-secondary">{room.description}</p>
                        <p>Up to {room.capacity} guest(s)</p>
                        <Button
                          variant={selectedIds.includes(room.id) ? 'success' : 'outline-success'}
                          onClick={() => toggle(room.id)}
                        >
                          {selectedIds.includes(room.id) ? 'Selected' : 'Select room'}
                        </Button>
                      </Card.Body>
                    </Card>
                  </Col>
                ))
              ) : (
                <Col>
                  <Alert variant="info">No rooms found for these filters.</Alert>
                </Col>
              )}
            </Row>
          )}
        </Col>
        <Col lg={4}>
          <Card className="booking-panel">
            <Card.Body className="p-4">
              <h2>Your stay</h2>
              <Form onSubmit={searchAvailable}>
                <Form.Group className="mb-3" controlId="stay-start">
                  <Form.Label>Check-in</Form.Label>
                  <Form.Control
                    type="date"
                    required
                    min={day(0)}
                    value={dates.startDate}
                    onChange={(event) => {
                      setDates({ ...dates, startDate: event.target.value });
                      setSelectedIds([]);
                    }}
                  />
                </Form.Group>
                <Form.Group className="mb-3" controlId="stay-end">
                  <Form.Label>Check-out</Form.Label>
                  <Form.Control
                    type="date"
                    required
                    min={dates.startDate || day(0)}
                    value={dates.endDate}
                    onChange={(event) => {
                      setDates({ ...dates, endDate: event.target.value });
                      setSelectedIds([]);
                    }}
                  />
                </Form.Group>
                <Button type="submit" variant="outline-secondary" className="w-100 mb-3">
                  Check availability
                </Button>
              </Form>
              <hr />
              <p>
                {selected.length} room(s) selected · {nights} night(s)
              </p>
              <p className="h4 mb-3">{money(estimate)}</p>
              <p className="small text-secondary">
                Estimate based on the listed rates. The server confirms availability and the final
                total when booking.
              </p>
              {user?.role === 'CUSTOMER' ? (
                <Button
                  className="w-100"
                  variant="success"
                  disabled={busy || !selected.length}
                  onClick={reserve}
                >
                  {busy ? 'Booking...' : 'Confirm booking'}
                </Button>
              ) : (
                <Button as={Link} to="/login" className="w-100" variant="success">
                  Sign in as customer to book
                </Button>
              )}
            </Card.Body>
          </Card>
        </Col>
      </Row>
    </>
  );
}
