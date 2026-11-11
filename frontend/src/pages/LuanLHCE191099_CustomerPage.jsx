import { useState } from 'react';
import { Alert, Badge, Button, Form, Modal, Spinner, Table } from 'react-bootstrap';
import { errorText } from '../api/LuanLHCE191099_axiosClient.js';
import { customerApi } from '../api/LuanLHCE191099_customerApi.js';
import useApiList from '../hooks/LuanLHCE191099_useApiList.js';

export const customerFields = [
  { name: 'fullName', label: 'Full name', required: true, maxLength: 100 },
  { name: 'telephone', label: 'Telephone', type: 'tel', required: true, maxLength: 20 },
  { name: 'email', label: 'Email', type: 'email', required: true, maxLength: 150 },
  { name: 'birthday', label: 'Birthday', type: 'date', required: true },
  {
    name: 'password',
    label: 'Password',
    type: 'password',
    required: true,
    maxLength: 100,
    help: 'Leave blank when editing to keep the current password.',
  },
];

export default function CustomerPage() {
  const [search, setSearch] = useState('');
  const customers = useApiList(customerApi.getAll, search);
  const [form, setForm] = useState(null);
  const [editingId, setEditingId] = useState(null);
  const [deleting, setDeleting] = useState(null);
  const [busy, setBusy] = useState(false);
  const [formError, setFormError] = useState('');
  const [notice, setNotice] = useState('');

  function handleOpenCreate() {
    setEditingId(null);
    setForm({
      fullName: '',
      telephone: '',
      email: '',
      birthday: '',
      password: '',
      status: 1,
    });
    setFormError('');
  }

  function handleOpenEdit(customer) {
    setEditingId(customer.id);
    setForm({
      fullName: customer.fullName,
      telephone: customer.telephone,
      email: customer.email,
      birthday: customer.birthday,
      password: '',
      status: customer.status,
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
      const body = { ...form, status: Number(form.status) };
      if (editingId === null) {
        await customerApi.create(body);
        setNotice('Record created.');
      } else {
        await customerApi.update(editingId, body);
        setNotice('Changes saved.');
      }
      setForm(null);
      customers.reload();
    } catch (error) {
      setFormError(errorText(error));
    } finally {
      setBusy(false);
    }
  }

  function handleOpenDelete(customer) {
    setDeleting(customer);
    setFormError('');
  }

  async function handleConfirmDelete() {
    setBusy(true);
    setFormError('');
    try {
      await customerApi.delete(deleting.id);
      setNotice('Record deleted.');
      setDeleting(null);
      customers.reload();
    } catch (error) {
      setFormError(errorText(error));
    } finally {
      setBusy(false);
    }
  }

  return (
    <section>
      <div className="page-heading">
        <div>
          <h1>Customer management</h1>
          <p className="text-secondary mb-0">{customers.data.length} record(s)</p>
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
      {customers.error && (
        <Alert variant="danger">
          {customers.error}{' '}
          <Button size="sm" variant="outline-danger" onClick={customers.reload}>
            Retry
          </Button>
        </Alert>
      )}
      {customers.loading ? (
        <Spinner animation="border" role="status">
          <span className="visually-hidden">Loading</span>
        </Spinner>
      ) : (
        <div className="table-shell">
          <Table responsive hover className="mb-0 align-middle">
            <thead>
              <tr>
                  <th>ID</th>
                  <th>Full name</th>
                  <th>Email</th>
                  <th>Telephone</th>
                  <th>Birthday</th>
                  <th>Status</th>
                  <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              {customers.data.length ? (
                customers.data.map((customer) => (
                  <tr key={customer.id}>
                    <td>{customer.id}</td>
                    <td>{customer.fullName}</td>
                    <td>{customer.email}</td>
                    <td>{customer.telephone}</td>
                    <td>{customer.birthday}</td>
                    <td>
                      <Badge bg={customer.status === 1 ? 'success' : 'secondary'}>
                        {customer.status === 1 ? 'Active' : 'Inactive'}
                      </Badge>
                    </td>
                    <td className="text-nowrap">
                      <Button
                        size="sm"
                        variant="outline-primary"
                        className="me-2"
                        onClick={() => handleOpenEdit(customer)}
                      >
                        Edit
                      </Button>
                      <Button
                        size="sm"
                        variant="outline-danger"
                        onClick={() => handleOpenDelete(customer)}
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
              {editingId === null ? 'Create' : 'Edit'} — Customer management
            </Modal.Title>
          </Modal.Header>
          <Modal.Body>
            {formError && <Alert variant="danger">{formError}</Alert>}
            {form && (
              <>
                <Form.Group className="mb-3" controlId="field-fullName">
                  <Form.Label>Full name *</Form.Label>
                  <Form.Control
                    name="fullName"
                    value={form.fullName}
                    onChange={handleChange}
                    required
                    maxLength={100}
                  />
                </Form.Group>
                <Form.Group className="mb-3" controlId="field-telephone">
                  <Form.Label>Telephone *</Form.Label>
                  <Form.Control
                    name="telephone"
                    type="tel"
                    value={form.telephone}
                    onChange={handleChange}
                    required
                    maxLength={20}
                  />
                </Form.Group>
                <Form.Group className="mb-3" controlId="field-email">
                  <Form.Label>Email *</Form.Label>
                  <Form.Control
                    name="email"
                    type="email"
                    value={form.email}
                    onChange={handleChange}
                    required
                    maxLength={150}
                  />
                </Form.Group>
                <Form.Group className="mb-3" controlId="field-birthday">
                  <Form.Label>Birthday *</Form.Label>
                  <Form.Control
                    name="birthday"
                    type="date"
                    value={form.birthday}
                    onChange={handleChange}
                    required
                  />
                </Form.Group>
                <Form.Group className="mb-3" controlId="field-password">
                  <Form.Label>{editingId === null ? 'Password *' : 'Password'}</Form.Label>
                  <Form.Control
                    name="password"
                    type="password"
                    value={form.password}
                    onChange={handleChange}
                    required={editingId === null}
                    minLength={6}
                    maxLength={100}
                    autoComplete="new-password"
                  />
                  <Form.Text muted>Leave blank when editing to keep the current password.</Form.Text>
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
            A customer with booking history cannot be deleted. You can edit the account and set it inactive.
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
