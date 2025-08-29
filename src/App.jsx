import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import NavBar from './components/NavBar.jsx';
import RecipeList from './pages/RecipeList.jsx';
import RecipeDetail from './pages/RecipeDetail.jsx';
import MyRecipes from './pages/MyRecipes.jsx';
import EditRecipe from './pages/EditRecipe.jsx';
import MyRecipeDetail from './pages/MyRecipeDetail.jsx';

export default function App() {
  return (
    <BrowserRouter>
      <div style={{ maxWidth: 960, margin: '0 auto', padding: '0 16px' }}>
        <NavBar />
        <Routes>
          <Route path="/" element={<RecipeList />} />
          <Route path="/recipes/:id" element={<RecipeDetail />} /> {/* ← use real component */}
          <Route path="/my-recipes" element={<MyRecipes />} />
          {/* other routes can stay */}
          <Route path="*" element={<Navigate to="/" replace />} />
          <Route path="/my-recipes/:id" element={<MyRecipeDetail />} />
          <Route path="/my-recipes/:id/edit" element={<EditRecipe />} />
        </Routes>
      </div>
    </BrowserRouter>
  );
}
