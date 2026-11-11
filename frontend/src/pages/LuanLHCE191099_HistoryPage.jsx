import { useState } from 'react';
import { Alert, Button, Form, Spinner, Table } from 'react-bootstrap';
import { bookingApi } from '../api/LuanLHCE191099_bookingApi.js';
import useApiList from '../hooks/LuanLHCE191099_useApiList.js';
import { bookingColumns } from './LuanLHCE191099_BookingPage.jsx';

export default function HistoryPage() {
  const [search, setSearch] = useState('');
  const bookings = useApiList(bookingApi.getMine, search);

  return (
    <section>
      <div className="page-heading">
        <div>
          <h1>My booking history</h1>
          <p className="text-secondary mb-0">{bookings.data.length} record(s)</p>
        </div>
      </div>
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
      {bookings.error && (
        <Alert variant="danger">
          {bookings.error}{' '}
          <Button size="sm" variant="outline-danger" onClick={bookings.reload}>
            Retry
          </Button>
        </Alert>
      )}
      {bookings.loading ? (
        <Spinner animation="border" role="status">
          <span className="visually-hidden">Loading</span>
        </Spinner>
      ) : (
        <div className="table-shell">
          <Table responsive hover className="mb-0 align-middle">
            <thead>
              <tr>
                {bookingColumns.map((column) => (
                  <th key={column.key}>{column.label}</th>
                ))}
              </tr>
            </thead>
            <tbody>
              {bookings.data.length ? (
                bookings.data.map((booking) => (
                  <tr key={booking.id}>
                    {bookingColumns.map((column) => (
                      <td key={column.key}>
                        {column.render
                          ? column.render(booking[column.key], booking)
                          : String(booking[column.key] ?? '—')}
                      </td>
                    ))}
                  </tr>
                ))
              ) : (
                <tr>
                  <td colSpan={bookingColumns.length} className="text-center py-4 text-secondary">
                    No records found.
                  </td>
                </tr>
              )}
            </tbody>
          </Table>
        </div>
      )}
    </section>
  );
}
