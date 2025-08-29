import { useEffect, useState } from 'react';
import { useParams, Link } from 'react-router-dom';
import api from '../services/api';

export default function RecipeDetail() {
  const { id } = useParams();

  const [recipe, setRecipe] = useState(null);
  const [loading, setLoading] = useState(true);
  const [loadError, setLoadError] = useState('');
  const [saving, setSaving] = useState(false);
  const [savedId, setSavedId] = useState(null);

  useEffect(() => {
    let cancelled = false;
    (async () => {
      setLoading(true);
      setLoadError('');
      try {
        const { data } = await api.get(`/mealdb/${id}`);
        if (!cancelled) setRecipe(data);
      } catch (err) {
        console.error(err);
        if (!cancelled) setLoadError('Unable to load recipe.');
      } finally {
        if (!cancelled) setLoading(false);
      }
    })();
    return () => { cancelled = true; };
  }, [id]);

  const saveToMyRecipes = async () => {
    if (!recipe) return;
    try {
      setSaving(true);
      const res = await api.post('/recipes/save', recipe, { validateStatus: () => true });
      if (res.status === 201) {
        const loc = res.headers['location'] || res.headers['Location'];
        const newId = loc ? loc.split('/').pop() : null;
        setSavedId(newId);
        alert('Saved to My Recipes!');
      } else {
        alert(`Save failed (status ${res.status}).`);
      }
    } catch (e) {
      console.error(e);
      alert('Save failed. See console/logs for details.');
    } finally {
      setSaving(false);
    }
  };

  if (loading) return <p style={{ maxWidth: 900, margin: '24px auto' }}>Loading…</p>;
  if (loadError || !recipe) {
    return (
      <div style={{ maxWidth: 900, margin: '24px auto' }}>
        <Link to="/">← Back</Link>
        <p style={{ color: 'crimson' }}>{loadError || 'Recipe not found.'}</p>
      </div>
    );
  }

  return (
    <div style={{ maxWidth: 900, margin: '24px auto' }}>
      <Link to="/">← Back</Link>
      <h2 style={{ margin: '8px 0 0' }}>{recipe.title}</h2>

      <div style={{ display: 'flex', gap: 12, alignItems: 'center', margin: '8px 0 16px' }}>
        <button onClick={saveToMyRecipes} disabled={saving}>
          {saving ? 'Saving…' : 'Save to My Recipes'}
        </button>
        {savedId && (
          <Link to={`/my-recipes/${savedId}`} style={{ textDecoration: 'none' }}>
            View saved copy →
          </Link>
        )}
      </div>

      {recipe.imageUrl && (
        <img
          src={recipe.imageUrl}
          alt={recipe.title}
          style={{ maxWidth: '100%', borderRadius: 6, marginBottom: 12 }}
        />
      )}

      <p><b>Cook:</b> {recipe.cookMinutes ?? 0} min • <b>Servings:</b> {recipe.servings ?? 0}</p>

      <h3>Ingredients</h3>
      <ul>
        {(recipe.ingredients || []).map((ri, i) => (
          <li key={i}>
            {ri.quantity ? `${ri.quantity} ` : ''}{ri.unit} {ri.name}
          </li>
        ))}
      </ul>

      <h3>Steps</h3>
      <ol>
        {(recipe.steps || []).map((s, i) => (
          <li key={i}>{s}</li>
        ))}
      </ol>
    </div>
  );
}
