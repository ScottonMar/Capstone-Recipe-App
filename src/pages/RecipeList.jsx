import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import api from '../services/api';

export default function RecipeList() {
  const [q, setQ] = useState('chicken');
  const [list, setList] = useState([]);
  const [loading, setLoading] = useState(false);

  const search = async () => {
    setLoading(true);
    try {
      const { data } = await api.get(`/mealdb/search?q=${encodeURIComponent(q)}`);
      setList(data || []);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
  search();
  // eslint-disable-next-line react-hooks/exhaustive-deps
}, []);


  return (
    <div style={{ padding:'16px 0' }}>
      <h2>Search Recipes</h2>
      <div style={{ display:'flex', gap:8, marginBottom:12 }}>
        <input value={q} onChange={e=>setQ(e.target.value)} placeholder="Search term…" />
        <button onClick={search} disabled={loading}>{loading ? 'Searching…' : 'Search'}</button>
      </div>
      {list.length === 0 ? <p>No results.</p> : (
        <ul>
          {list.map(r => (
            <li key={r.id}><Link to={`/recipes/${r.id}`}>{r.title}</Link></li>
          ))}
        </ul>
      )}
    </div>
  );
}
