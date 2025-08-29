import { useEffect, useMemo, useState } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import api from '../services/api';

function EditRecipe() {
  const { id } = useParams();
  const navigate = useNavigate();
  const [loading, setLoading] = useState(true);
  const [title, setTitle] = useState('');
  const [imageUrl, setImageUrl] = useState('');
  const [cookMinutes, setCookMinutes] = useState(0);
  const [servings, setServings] = useState(0);
  const [ingredients, setIngredients] = useState([]);
  const [steps, setSteps] = useState([]);
  const [saving, setSaving] = useState(false);
  const [deleting, setDeleting] = useState(false);

  // preview state
  const [imgOk, setImgOk] = useState(true);
  const hasImage = imageUrl.trim().length > 0;

  useEffect(() => {
    let cancelled = false;
    (async () => {
      try {
        const { data } = await api.get(`/recipes/${id}`);
        if (cancelled) return;
        setTitle(data.title || '');
        setImageUrl(data.imageUrl || '');
        setCookMinutes(Number(data.cookMinutes ?? 0));
        setServings(Number(data.servings ?? 0));
        setIngredients(
          (data.ingredients || []).map(x => ({
            name: x.name || '',
            unit: x.unit || '',
            quantity: Number(x.quantity || 0),
          }))
        );
        setSteps(data.steps || []);
      } finally {
        if (!cancelled) setLoading(false);
      }
    })();
    return () => { cancelled = true; };
  }, [id]);

  const canSave = useMemo(
    () => !!title.trim() && !Number.isNaN(cookMinutes) && !Number.isNaN(servings),
    [title, cookMinutes, servings]
  );

  // ——— ingredients helpers ———
  const addIngredient = () => setIngredients(p => [...p, { name: '', unit: '', quantity: 0 }]);
  const removeIngredient = i => setIngredients(p => p.filter((_, idx) => idx !== i));
  const updateIngredient = (i, k, v) => setIngredients(p => p.map((it, idx) => (idx === i ? { ...it, [k]: v } : it)));

  // ——— steps helpers ———
  const addStep = () => setSteps(p => [...p, '']);
  const removeStep = i => setSteps(p => p.filter((_, idx) => idx !== i));
  const updateStep = (i, v) => setSteps(p => p.map((s, idx) => (idx === i ? v : s)));

  const onSave = async () => {
    if (!canSave) return;
    const payload = {
      title: title.trim(),
      imageUrl: imageUrl.trim(),
      cookMinutes: Number(cookMinutes || 0),
      servings: Number(servings || 0),
      ingredients: ingredients.map(i => ({
        name: i.name.trim(),
        unit: i.unit.trim(),
        quantity: Number(i.quantity || 0),
      })),
      steps: steps.map(s => (s || '').trim()).filter(Boolean),
    };
    try {
      setSaving(true);
      const res = await api.put(`/recipes/${id}`, payload, { validateStatus: () => true });
      if (res.status === 204) {
        alert('Saved!');
        navigate(`/my-recipes/${id}`);
      } else if (res.status === 404) {
        alert('Not found.');
        navigate('/my-recipes');
      } else {
        alert(`Save failed (${res.status}).`);
      }
    } finally {
      setSaving(false);
    }
  };

  const onDelete = async () => {
    if (!window.confirm('Delete this recipe?')) return;
    try {
      setDeleting(true);
      const res = await api.delete(`/recipes/${id}`, { validateStatus: () => true });
      if (res.status === 204) {
        alert('Deleted.');
        navigate('/my-recipes');
      } else {
        alert(`Delete failed (${res.status}).`);
      }
    } finally {
      setDeleting(false);
    }
  };

  if (loading) return <p className="container">Loading…</p>;

  return (
    <div className="container">
      <div style={{ display:'flex', justifyContent:'space-between', alignItems:'center' }}>
        <div>
          <Link to={`/my-recipes/${id}`}>← Back</Link>
          <h2 style={{ margin:'8px 0 0' }}>Edit Recipe</h2>
        </div>
        <div style={{ display:'flex', gap:8 }}>
          <button onClick={onSave} disabled={!canSave || saving}>{saving ? 'Saving…' : 'Save'}</button>
          <button onClick={onDelete} disabled={deleting} className="danger">{deleting ? 'Deleting…' : 'Delete'}</button>
        </div>
      </div>

      <section style={{ marginTop:16 }}>
        <label style={{ display:'block', marginBottom:8 }}>
          <div>Title</div>
          <input value={title} onChange={e => setTitle(e.target.value)} style={{ width:'100%' }} />
        </label>

        <label style={{ display:'block', marginBottom:8 }}>
          <div>Image URL</div>
          <input
            value={imageUrl}
            onChange={e => { setImageUrl(e.target.value); setImgOk(true); }}
            placeholder="https://…"
            style={{ width:'100%' }}
          />
          {/* 👇 Live image preview (stretch goal) */}
          {hasImage && (
            <div className="img-preview-wrap">
              <img
                className="img-preview"
                src={imageUrl}
                alt="preview"
                onLoad={() => setImgOk(true)}
                onError={() => setImgOk(false)}
              />
              {!imgOk && (
                <div className="img-preview-note">
                  Couldn’t load this image. Check the URL or try a different one.
                </div>
              )}
              {imgOk && (
                <div className="img-preview-note">
                  Preview loaded from the URL above.
                </div>
              )}
            </div>
          )}
        </label>

        <div style={{ display:'flex', gap:12, marginBottom:8 }}>
          <label>
            <div>Cook Minutes</div>
            <input type="number" value={cookMinutes} onChange={e => setCookMinutes(Number(e.target.value))} min={0} style={{ width:140 }} />
          </label>
          <label>
            <div>Servings</div>
            <input type="number" value={servings} onChange={e => setServings(Number(e.target.value))} min={0} style={{ width:140 }} />
          </label>
        </div>
      </section>

      <section style={{ marginTop:20 }}>
        <h3>Ingredients</h3>
        {ingredients.length === 0 && <p>No ingredients yet.</p>}
        <div style={{ display:'grid', gap:8 }}>
          {ingredients.map((ing, idx) => (
            <div key={idx} style={{ display:'grid', gridTemplateColumns:'2fr 1fr 1fr auto', gap:8, alignItems:'center' }}>
              <input placeholder="Name" value={ing.name} onChange={e => updateIngredient(idx,'name',e.target.value)} />
              <input placeholder="Unit" value={ing.unit} onChange={e => updateIngredient(idx,'unit',e.target.value)} />
              <input type="number" placeholder="Qty" value={ing.quantity} min={0} step="0.01" onChange={e => updateIngredient(idx,'quantity',Number(e.target.value))} />
              <button onClick={() => removeIngredient(idx)} className="danger">Remove</button>
            </div>
          ))}
        </div>
        <button onClick={addIngredient} style={{ marginTop:8 }}>+ Add Ingredient</button>
      </section>

      <section style={{ marginTop:20 }}>
        <h3>Steps</h3>
        {steps.length === 0 && <p>No steps yet.</p>}
        <div style={{ display:'grid', gap:8 }}>
          {steps.map((s, idx) => (
            <div key={idx} style={{ display:'grid', gridTemplateColumns:'1fr auto', gap:8, alignItems:'start' }}>
              <textarea value={s} onChange={e => updateStep(idx, e.target.value)} rows={2} />
              <button onClick={() => removeStep(idx)} className="danger" style={{ height:'fit-content' }}>Remove</button>
            </div>
          ))}
        </div>
        <button onClick={addStep} style={{ marginTop:8 }}>+ Add Step</button>
      </section>
    </div>
  );
}

export default EditRecipe;
