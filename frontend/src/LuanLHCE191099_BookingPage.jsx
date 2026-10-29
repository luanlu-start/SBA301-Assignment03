import { useState } from 'react';
import { Alert, Badge, Button, Col, Form, Modal, Row, Spinner, Table } from 'react-bootstrap';
import api, { errorText, money } from './LuanLHCE191099_api.js';
import useApiList from './LuanLHCE191099_useApiList.js';
export const bookingColumns = [
  { key: 'id', label: 'Booking' },
  { key: 'customerName', label: 'Customer' },
  {
    key: 'details',
    label: 'Rooms and dates',
    render: (details) => (
      <div className="history-detail">
        {details.map((detail) => (
          <div key={detail.roomId}>
            Room {detail.roomNumber}: {detail.startDate} → {detail.endDate}
            <br />
            {money(detail.actualPrice)} / night
          </div>
        ))}
      </div>
    ),
  },
  { key: 'totalPrice', label: 'Total', render: money },
  {
    key: 'status',
    label: 'Status',
    render: (value) => (
      <Badge bg={value === 1 ? 'success' : 'secondary'}>
        {value === 1 ? 'Confirmed' : 'Cancelled'}
      </Badge>
    ),
  },
];
export default function BookingPage() {
  const [search, setSearch] = useState('');
  const bookings = useApiList(`/bookings?q=${encodeURIComponent(search)}`);
  const customers = useApiList('/customers');
  const rooms = useApiList('/rooms');
  const [form, setForm] = useState(null);
  const [editingId, setEditingId] = useState(null);
  const [deleting, setDeleting] = useState(null);
  const [error, setError] = useState('');
  const [notice, setNotice] = useState('');
  const [busy, setBusy] = useState(false);
  function open(row = null) {
    setEditingId(row?.id ?? null);
    setError('');
    setForm(
      row
        ? {
            customerId: row.customerId,
            startDate: row.details[0]?.startDate || '',
            endDate: row.details[0]?.endDate || '',
            roomIds: row.details.map((detail) => detail.roomId),
            status: row.status,
          }
        : { customerId: '', startDate: '', endDate: '', roomIds: [], status: 1 },
    );
  }
  async function save(event) {
    event.preventDefault();
    setError('');
    if (!form.roomIds.length) {
      setError('Select at least one room.');
      return;
    }
    if (form.endDate <= form.startDate) {
      setError('Check-out must be after check-in.');
      return;
    }
    setBusy(true);
    try {
      const body = { ...form, customerId: Number(form.customerId), status: Number(form.status) };
      if (editingId === null) await api.post('/bookings', body);
      else await api.put(`/bookings/${editingId}`, body);
      setForm(null);
      setNotice(editingId === null ? 'Reservation created.' : 'Reservation updated.');
      bookings.reload();
    } catch (error) {
      setError(errorText(error));
    } finally {
      setBusy(false);
    }
  }
  async function remove() {
    setBusy(true);
    setError('');
    try {
      await api.delete(`/bookings/${deleting.id}`);
      setDeleting(null);
      setNotice('Reservation deleted.');
      bookings.reload();
    } catch (error) {
      setError(errorText(error));
    } finally {
      setBusy(false);
    }
  }
  function toggle(id) {
    setForm({
      ...form,
      roomIds: form.roomIds.includes(id)
        ? form.roomIds.filter((value) => value !== id)
        : [...form.roomIds, id],
    });
  }
  return (
    <section>
      <div className="page-heading">
        <div>
          <h1>Booking management</h1>
          <p className="text-secondary mb-0">Online and offline reservations</p>
        </div>
        <Button onClick={() => open()}>Create reservation</Button>
      </div>
      {notice && (
        <Alert variant="success" dismissible onClose={() => setNotice('')}>
          {notice}
        </Alert>
      )}
      <Form.Label htmlFor="booking-search" className="visually-hidden">
        Search bookings
      </Form.Label>
      <Form.Control
        id="booking-search"
        className="search-box mb-4"
        value={search}
        onChange={(event) => setSearch(event.target.value)}
        placeholder="Search ID, customer name or email..."
      />
      {(bookings.error || customers.error || rooms.error) && (
        <Alert variant="danger">{bookings.error || customers.error || rooms.error}</Alert>
      )}
      {bookings.loading ? (
        <Spinner animation="border" />
      ) : (
        <div className="table-shell">
          <Table responsive hover className="mb-0 align-middle">
            <thead>
              <tr>
                {bookingColumns.map((column) => (
                  <th key={column.key}>{column.label}</th>
                ))}
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              {bookings.data.length ? (
                bookings.data.map((row) => (
                  <tr key={row.id}>
                    {bookingColumns.map((column) => (
                      <td key={column.key}>
                        {column.render ? column.render(row[column.key], row) : row[column.key]}
                      </td>
                    ))}
                    <td className="text-nowrap">
                      <Button
                        size="sm"
                        variant="outline-primary"
                        className="me-2"
                        onClick={() => open(row)}
                      >
                        Edit
                      </Button>
                      <Button
                        size="sm"
                        variant="outline-danger"
                        onClick={() => {
                          setDeleting(row);
                          setError('');
                        }}
                      >
                        Delete
                      </Button>
                    </td>
                  </tr>
                ))
              ) : (
                <tr>
                  <td colSpan="6" className="text-center py-4">
                    No reservations found.
                  </td>
                </tr>
              )}
            </tbody>
          </Table>
        </div>
      )}
      <Modal show={form !== null} onHide={() => !busy && setForm(null)} size="lg" backdrop="static">
        <Form onSubmit={save}>
          <Modal.Header closeButton={!busy}>
            <Modal.Title>
              {editingId === null ? 'Create reservation' : `Edit reservation #${editingId}`}
            </Modal.Title>
          </Modal.Header>
          <Modal.Body>
            {error && <Alert variant="danger">{error}</Alert>}
            {form && (
              <>
                <Form.Group className="mb-3" controlId="booking-customer">
                  <Form.Label>Customer *</Form.Label>
                  <Form.Select
                    value={form.customerId}
                    required
                    onChange={(event) => setForm({ ...form, customerId: event.target.value })}
                  >
                    <option value="">Select customer...</option>
                    {customers.data.map((customer) => (
                      <option key={customer.id} value={customer.id}>
                        {customer.fullName} — {customer.email}
                        {customer.status === 0 ? ' (Inactive)' : ''}
                      </option>
                    ))}
                  </Form.Select>
                </Form.Group>
                <Row>
                  <Col>
                    <Form.Group className="mb-3" controlId="booking-start">
                      <Form.Label>Check-in *</Form.Label>
                      <Form.Control
                        type="date"
                        value={form.startDate}
                        required
                        onChange={(event) => setForm({ ...form, startDate: event.target.value })}
                      />
                    </Form.Group>
                  </Col>
                  <Col>
                    <Form.Group className="mb-3" controlId="booking-end">
                      <Form.Label>Check-out *</Form.Label>
                      <Form.Control
                        type="date"
                        value={form.endDate}
                        required
                        onChange={(event) => setForm({ ...form, endDate: event.target.value })}
                      />
                    </Form.Group>
                  </Col>
                </Row>
                <Form.Group className="mb-3" controlId="booking-status">
                  <Form.Label>Status</Form.Label>
                  <Form.Select
                    value={form.status}
                    onChange={(event) => setForm({ ...form, status: event.target.value })}
                  >
                    <option value={1}>Confirmed</option>
                    <option value={0}>Cancelled</option>
                  </Form.Select>
                </Form.Group>
                <fieldset>
                  <legend className="fs-6">Select one or more rooms *</legend>
                  {rooms.data.map((room) => (
                    <Form.Check
                      key={room.id}
                      id={`booking-room-${room.id}`}
                      className="mb-2"
                      checked={form.roomIds.includes(room.id)}
                      onChange={() => toggle(room.id)}
                      label={`Room ${room.number} — ${room.roomTypeName} — ${money(room.price)} / night${room.status === 0 ? ' (Inactive)' : ''}`}
                    />
                  ))}
                </fieldset>
                <p className="text-secondary small mt-3 mb-0">
                  The server checks room availability and calculates the total. Check-out is
                  exclusive: another stay may begin on that date.
                </p>
              </>
            )}
          </Modal.Body>
          <Modal.Footer>
            <Button variant="secondary" disabled={busy} onClick={() => setForm(null)}>
              Cancel
            </Button>
            <Button type="submit" disabled={busy}>
              {busy ? 'Saving...' : 'Save reservation'}
            </Button>
          </Modal.Footer>
        </Form>
      </Modal>
      <Modal show={deleting !== null} onHide={() => !busy && setDeleting(null)}>
        <Modal.Header closeButton={!busy}>
          <Modal.Title>Confirm delete</Modal.Title>
        </Modal.Header>
        <Modal.Body>
          {error && <Alert variant="danger">{error}</Alert>}Delete reservation #{deleting?.id} and
          its booking details?
        </Modal.Body>
        <Modal.Footer>
          <Button variant="secondary" disabled={busy} onClick={() => setDeleting(null)}>
            Cancel
          </Button>
          <Button variant="danger" disabled={busy} onClick={remove}>
            Delete
          </Button>
        </Modal.Footer>
      </Modal>
    </section>
  );
}
