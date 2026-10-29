import { Alert } from 'react-bootstrap';
import CrudPage, { activeOptions, statusBadge } from './LuanLHCE191099_CrudPage.jsx';
import useApiList from './LuanLHCE191099_useApiList.js';
import { money } from './LuanLHCE191099_api.js';
const columns = [
  { key: 'id', label: 'ID' },
  { key: 'number', label: 'Room' },
  { key: 'roomTypeName', label: 'Type' },
  { key: 'capacity', label: 'Capacity' },
  { key: 'price', label: 'Price / night', render: money },
  { key: 'status', label: 'Status', render: statusBadge },
];
export default function RoomPage() {
  const types = useApiList('/public/room-types');
  const fields = [
    { name: 'number', label: 'Room number', required: true, maxLength: 20 },
    { name: 'description', label: 'Description', type: 'textarea', maxLength: 1000 },
    {
      name: 'capacity',
      label: 'Maximum capacity',
      type: 'number',
      numeric: true,
      required: true,
      min: 1,
      max: 20,
      step: 1,
    },
    {
      name: 'roomTypeId',
      label: 'Room type',
      type: 'select',
      numeric: true,
      required: true,
      options: types.data.map((type) => ({ value: type.id, label: type.name })),
    },
    {
      name: 'price',
      label: 'Price per night (VND)',
      type: 'number',
      numeric: true,
      required: true,
      min: 0.01,
      step: 0.01,
    },
    {
      name: 'status',
      label: 'Status',
      type: 'select',
      numeric: true,
      required: true,
      options: activeOptions,
    },
  ];
  return (
    <>
      {types.error && <Alert variant="danger">{types.error}</Alert>}
      <CrudPage
        title="Room management"
        endpoint="/rooms"
        fields={fields}
        columns={columns}
        defaults={{ status: 1, capacity: 2 }}
        deleteHint="A room with booking history will be set inactive instead of being removed."
      />
    </>
  );
}
