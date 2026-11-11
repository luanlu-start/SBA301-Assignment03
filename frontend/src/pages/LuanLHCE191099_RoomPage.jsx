import { useState } from 'react';
import { Alert, Badge, Button, Form, Modal, Spinner, Table } from 'react-bootstrap';
import { errorText } from '../api/LuanLHCE191099_axiosClient.js';
import { roomApi } from '../api/LuanLHCE191099_roomApi.js';
import useApiList from '../hooks/LuanLHCE191099_useApiList.js';
import { money } from '../utils/LuanLHCE191099_money.js';

export default function RoomPage() {
  const [search, setSearch] = useState('');
  const rooms = useApiList(roomApi.getAll, search);
  const roomTypes = useApiList(roomApi.getTypes);
  const [form, setForm] = useState(null);
  const [editingId, setEditingId] = useState(null);
  const [deleting, setDeleting] = useState(null);
  const [busy, setBusy] = useState(false);
  const [formError, setFormError] = useState('');
  const [notice, setNotice] = useState('');

  function handleOpenCreate() {
    setEditingId(null);
    setForm({
      number: '',
      description: '',
      capacity: 2,
      roomTypeId: '',
      price: '',
      status: 1,
    });
    setFormError('');
  }

  function handleOpenEdit(room) {
    setEditingId(room.id);
    setForm({
      number: room.number,
      description: room.description ?? '',
      capacity: room.capacity,
      roomTypeId: room.roomTypeId,
      price: room.price,
      status: room.status,
    });
    setFormError('');
  }

  function handleChange(event) {
    const { name, value } = event.target;
    setForm((currentForm) => ({ ...currentForm, [name]: value }));
  }

  async function handleSave(event) {
    event.preventDefault();
    setBusy(true);
    setFormError('');
    try {
      const body = {
        ...form,
        capacity: Number(form.capacity),
        roomTypeId: Number(form.roomTypeId),
        price: Number(form.price),
        status: Number(form.status),
      };
      if (editingId === null) {
        await roomApi.create(body);
        setNotice('Record created.');
      } else {
        await roomApi.update(editingId, body);
        setNotice('Changes saved.');
      }
      setForm(null);
      rooms.reload();
    } catch (error) {
      setFormError(errorText(error));
    } finally {
      setBusy(false);
    }
  }

  function handleOpenDelete(room) {
    setDeleting(room);
    setFormError('');
  }

  async function handleConfirmDelete() {
    setBusy(true);
    setFormError('');
    try {
      const { data } = await roomApi.delete(deleting.id);
      setNotice(data?.message || 'Record deleted.');
      setDeleting(null);
      rooms.reload();
    } catch (error) {
      setFormError(errorText(error));
    } finally {
      setBusy(false);
    }
  }

  return (
    <section>
      {roomTypes.error && <Alert variant="danger">{roomTypes.error}</Alert>}
      <div className="page-heading">
        <div>
          <h1>Room management</h1>
          <p className="text-secondary mb-0">{rooms.data.length} record(s)</p>
        </div>
        <Button onClick={handleOpenCreate}>Add new</Button>
      </div>
      {notice && (
        <Alert variant="success" dismissible onClose={() => setNotice('')}>
          {notice}
        </Alert>
      )}
      <Form.Label htmlFor="record-search" className="visually-hidden">
        Search records
      </Form.Label>
      <Form.Control
        id="record-search"
        className="search-box mb-4"
        placeholder="Search..."
        value={search}
        onChange={(event) => setSearch(event.target.value)}
      />
      {rooms.error && (
        <Alert variant="danger">
          {rooms.error}{' '}
          <Button size="sm" variant="outline-danger" onClick={rooms.reload}>
            Retry
          </Button>
        </Alert>
      )}
      {rooms.loading ? (
        <Spinner animation="border" role="status">
          <span className="visually-hidden">Loading</span>
        </Spinner>
      ) : (
        <div className="table-shell">
          <Table responsive hover className="mb-0 align-middle">
            <thead>
              <tr>
                  <th>ID</th>
                  <th>Room</th>
                  <th>Type</th>
                  <th>Capacity</th>
                  <th>Price / night</th>
                  <th>Status</th>
                  <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              {rooms.data.length ? (
                rooms.data.map((room) => (
                  <tr key={room.id}>
                    <td>{room.id}</td>
                    <td>{room.number}</td>
                    <td>{room.roomTypeName}</td>
                    <td>{room.capacity}</td>
                    <td>{money(room.price)}</td>
                    <td>
                      <Badge bg={room.status === 1 ? 'success' : 'secondary'}>
                        {room.status === 1 ? 'Active' : 'Inactive'}
                      </Badge>
                    </td>
                    <td className="text-nowrap">
                      <Button
                        size="sm"
                        variant="outline-primary"
                        className="me-2"
                        onClick={() => handleOpenEdit(room)}
                      >
                        Edit
                      </Button>
                      <Button
                        size="sm"
                        variant="outline-danger"
                        onClick={() => handleOpenDelete(room)}
                      >
                        Delete
                      </Button>
                    </td>
                  </tr>
                ))
              ) : (
                <tr>
                  <td colSpan={7} className="text-center py-4 text-secondary">
                    No records found.
                  </td>
                </tr>
              )}
            </tbody>
          </Table>
        </div>
      )}
      <Modal show={form !== null} onHide={() => !busy && setForm(null)} size="lg" backdrop="static">
        <Form onSubmit={handleSave}>
          <Modal.Header closeButton={!busy}>
            <Modal.Title>
              {editingId === null ? 'Create' : 'Edit'} — Room management
            </Modal.Title>
          </Modal.Header>
          <Modal.Body>
            {formError && <Alert variant="danger">{formError}</Alert>}
            {form && (
              <>
                <Form.Group className="mb-3" controlId="field-number">
                  <Form.Label>Room number *</Form.Label>
                  <Form.Control
                    name="number"
                    value={form.number}
                    onChange={handleChange}
                    required
                    maxLength={20}
                  />
                </Form.Group>
                <Form.Group className="mb-3" controlId="field-description">
                  <Form.Label>Description</Form.Label>
                  <Form.Control
                    as="textarea"
                    rows={3}
                    name="description"
                    value={form.description}
                    onChange={handleChange}
                    maxLength={1000}
                  />
                </Form.Group>
                <Form.Group className="mb-3" controlId="field-capacity">
                  <Form.Label>Maximum capacity *</Form.Label>
                  <Form.Control
                    name="capacity"
                    type="number"
                    value={form.capacity}
                    onChange={handleChange}
                    required
                    min={1}
                    max={20}
                    step={1}
                  />
                </Form.Group>
                <Form.Group className="mb-3" controlId="field-roomTypeId">
                  <Form.Label>Room type *</Form.Label>
                  <Form.Select
                    name="roomTypeId"
                    value={form.roomTypeId}
                    onChange={handleChange}
                    required
                  >
                    <option value="">Select...</option>
                    {roomTypes.data.map((type) => (
                      <option key={type.id} value={type.id}>
                        {type.name}
                      </option>
                    ))}
                  </Form.Select>
                </Form.Group>
                <Form.Group className="mb-3" controlId="field-price">
                  <Form.Label>Price per night (VND) *</Form.Label>
                  <Form.Control
                    name="price"
                    type="number"
                    value={form.price}
                    onChange={handleChange}
                    required
                    min={0.01}
                    step={0.01}
                  />
                </Form.Group>
                <Form.Group className="mb-3" controlId="field-status">
                  <Form.Label>Status *</Form.Label>
                  <Form.Select name="status" value={form.status} onChange={handleChange} required>
                    <option value={1}>Active</option>
                    <option value={0}>Inactive</option>
                  </Form.Select>
                </Form.Group>
              </>
            )}
          </Modal.Body>
          <Modal.Footer>
            <Button type="button" variant="secondary" disabled={busy} onClick={() => setForm(null)}>
              Cancel
            </Button>
            <Button type="submit" disabled={busy}>
              {busy ? 'Saving...' : 'Save'}
            </Button>
          </Modal.Footer>
        </Form>
      </Modal>
      <Modal show={deleting !== null} onHide={() => !busy && setDeleting(null)}>
        <Modal.Header closeButton={!busy}>
          <Modal.Title>Confirm delete</Modal.Title>
        </Modal.Header>
        <Modal.Body>
          {formError && <Alert variant="danger">{formError}</Alert>}
          Delete record #{deleting?.id}?
          <p className="mt-2 text-secondary mb-0">
            A room with booking history will be set inactive instead of being removed.
          </p>
        </Modal.Body>
        <Modal.Footer>
          <Button variant="secondary" disabled={busy} onClick={() => setDeleting(null)}>
            Cancel
          </Button>
          <Button variant="danger" disabled={busy} onClick={handleConfirmDelete}>
            {busy ? 'Deleting...' : 'Delete'}
          </Button>
        </Modal.Footer>
      </Modal>
    </section>
  );
}
