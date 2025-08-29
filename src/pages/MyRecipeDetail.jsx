import { useEffect, useState } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import api from '../services/api';

export default function MyRecipeDetail() {
  const { id } = useParams();
  const navigate = useNavigate();
  const [recipe, setRecipe] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    let cancel = false;
    (async () => {
      setLoading(true);
      const res = await api.get(`/recipes/${id}`, { validateStatus: () => true });
      if (cancel) return;
      if (res.status === 200) setRecipe(res.data);
      else setRecipe(null);
      setLoading(false);
    })();
    return () => { cancel = true; };
  }, [id]);

  async function onDelete() {
    if (!window.confirm('Delete this recipe?')) return;
    const res = await api.delete(`/recipes/${id}`, { validateStatus: () => true });
    if (res.status === 204) {
      alert('Deleted.');
      navigate('/my-recipes');
    } else {
      alert(`Delete failed (${res.status}).`);
    }
  }

  if (loading) return <p className="container">Loading…</p>;
  if (!recipe) return (
    <div className="container">
      <Link to="/my-recipes">← Back</Link>
      <p>Recipe not found.</p>
    </div>
  );

  return (
    <div className="container recipe-detail">
      <div style={{ display:'flex', justifyContent:'space-between', alignItems:'center', marginBottom:8 }}>
        <div style={{ display:'flex', gap:12, alignItems:'center' }}>
          <Link to="/my-recipes">← Back</Link>
          <Link to={`/my-recipes/${id}/edit`}><button style={{ background:'#28a745' }}>Edit</button></Link>
        </div>
        <button className="danger" onClick={onDelete}>Delete</button>
      </div>

      <h2 style={{ margin:'8px 0 12px' }}>{recipe.title}</h2>
      {recipe.imageUrl && <img src={recipe.imageUrl} alt={recipe.title || 'Recipe image'} />}
      <p><b>Cook:</b> {recipe.cookMinutes ?? 0} min • <b>Servings:</b> {recipe.servings ?? 0}</p>

      <h3>Ingredients</h3>
      {recipe.ingredients?.length ? (
        <ul>{recipe.ingredients.map((i, idx) => <li key={idx}>{i.quantity ? `${i.quantity} ` : ''}{i.unit} {i.name}</li>)}</ul>
      ) : <p>No ingredients.</p>}

      <h3>Steps</h3>
      {recipe.steps?.length ? (
        <ol>{recipe.steps.map((s, idx) => <li key={idx}>{s}</li>)}</ol>
      ) : <p>No steps.</p>}
    </div>
  );
}
