import { useEffect, useState } from 'react';
import { Alert, Button, Form, Spinner } from 'react-bootstrap';
import { errorText } from '../api/LuanLHCE191099_axiosClient.js';
import { profileApi } from '../api/LuanLHCE191099_profileApi.js';
import { useAuth } from '../context/LuanLHCE191099_AuthContext.jsx';
import FormFields from '../components/Common/LuanLHCE191099_FormFields.jsx';
export default function ProfilePage({ fields }) {
  const { refresh } = useAuth();
  const [form, setForm] = useState(null);
  const [error, setError] = useState('');
  const [saved, setSaved] = useState(false);
  const [busy, setBusy] = useState(false);
  useEffect(() => {
    let active = true;
    profileApi
      .get()
      .then(({ data }) => {
        if (active)
          setForm(
            Object.fromEntries(
              fields.map((field) => [
                field.name,
                field.name === 'password' ? '' : (data[field.name] ?? ''),
              ]),
            ),
          );
      })
      .catch((error) => {
        if (active) setError(errorText(error));
      });
    return () => {
      active = false;
    };
  }, [fields]);
  async function save(event) {
    event.preventDefault();
    setBusy(true);
    setSaved(false);
    setError('');
    try {
      await profileApi.update(form);
      await refresh();
      setForm({ ...form, password: '' });
      setSaved(true);
    } catch (error) {
      setError(errorText(error));
    } finally {
      setBusy(false);
    }
  }
  return (
    <section className="profile-shell">
      <h1>My profile</h1>
      <p className="text-secondary">Update your own contact information.</p>
      {error && <Alert variant="danger">{error}</Alert>}
      {saved && <Alert variant="success">Profile updated.</Alert>}
      {form ? (
        <Form onSubmit={save}>
          <FormFields fields={fields} value={form} onChange={setForm} editing />
          <Button type="submit" disabled={busy}>
            {busy ? 'Saving...' : 'Save profile'}
          </Button>
        </Form>
      ) : (
        !error && <Spinner animation="border" />
      )}
    </section>
  );
}
