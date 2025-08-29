import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import api from '../services/api';

export default function MyRecipes() {
  const [items, setItems] = useState([]);
  const [loading, setLoading] = useState(true);

  async function load() {
    setLoading(true);
    try {
      const { data } = await api.get('/recipes', { validateStatus: () => true });
      console.log('MyRecipes load status', data);
      setItems(Array.isArray(data) ? data : []);
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => { load(); }, []);

  async function onDelete(id) {
    if (!window.confirm('Delete this recipe?')) return;
    const res = await api.delete(`/recipes/${id}`, { validateStatus: () => true });
    if (res.status === 204) {
      setItems(prev => prev.filter(r => r.id !== id));
      alert('Deleted.');
    } else {
      alert(`Delete failed (${res.status}).`);
    }
  }

  if (loading) return <p className="container">Loading…</p>;

  return (
    <div className="container">
      <h2>My Recipes</h2>
      <p style={{color:'#666'}}>Count: {items.length}</p>

      {items.length === 0 && (
        <p>No saved recipes yet. Go to Search, open a result, and click “Save to My Recipes”.</p>
      )}

      <div style={{ display: 'grid', gap: 12 }}>
        {items.map((r, i) => (
          <div key={r.id ?? `row-${i}`} style={{ border: '1px solid #702a2aff', borderRadius: 6, padding: 12 }}>
            <div style={{ display: 'flex', justifyContent: 'space-between', gap: 12, alignItems: 'center' }}>
              <div>
                <h3 style={{ margin: 0 }}>{r.title}</h3>
                <div style={{ color: '#666', fontSize: '.9rem' }}>
                  id: {r.id} • {r.cookMinutes ?? 0} min • {r.servings ?? 0} servings
                </div>
              </div>
              <div style={{ display: 'flex', gap: 8 }}>
                <Link to={`/my-recipes/${r.id}`}><button>View</button></Link>
                <Link to={`/my-recipes/${r.id}/edit`}><button style={{ background:'#28a745' }}>Edit</button></Link>
                <button className="danger" onClick={() => onDelete(r.id)}>Delete</button>
              </div>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}
