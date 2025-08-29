import { Link, useLocation } from 'react-router-dom';

export default function NavBar() {
  const location = useLocation();
  const linkStyle = (path) => ({
    textDecoration: location.pathname === path ? 'underline' : 'none',
    fontWeight: location.pathname === path ? 'bold' : 'normal',
  });

  return (
    <nav style={{display:'flex', gap:'16px', padding:'12px 0', borderBottom:'1px solid #ddd', marginBottom:'16px'}}>
      <Link to="/" style={linkStyle('/')}>Search</Link>
      <Link to="/my-recipes" style={linkStyle('/my-recipes')}>My Recipes</Link>
    </nav>
  );
}
