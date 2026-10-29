import CrudPage, { activeOptions, statusBadge } from './LuanLHCE191099_CrudPage.jsx';
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
const fields = [
  ...customerFields,
  {
    name: 'status',
    label: 'Status',
    type: 'select',
    numeric: true,
    required: true,
    options: activeOptions,
  },
];
const columns = [
  { key: 'id', label: 'ID' },
  { key: 'fullName', label: 'Full name' },
  { key: 'email', label: 'Email' },
  { key: 'telephone', label: 'Telephone' },
  { key: 'birthday', label: 'Birthday' },
  { key: 'status', label: 'Status', render: statusBadge },
];
export default function CustomerPage() {
  return (
    <CrudPage
      title="Customer management"
      endpoint="/customers"
      fields={fields}
      columns={columns}
      defaults={{ status: 1 }}
      deleteHint="A customer with booking history cannot be deleted. You can edit the account and set it inactive."
    />
  );
}
