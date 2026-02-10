import { useEffect, useMemo, useState } from "react";
import axios from "axios";
import { ExternalLink, Github, Linkedin, Loader2 } from "lucide-react";

const API_BASE = import.meta.env.VITE_API_BASE || "http://localhost:8081";

const fallbackProfile = {
  name: "Fulano de Tal",
  title: "Computer Science Student (PUC Minas). Aspiring Fullstack Developer.",
  blurb: "Passionate about clean code and learning new technologies.",
  photoUrl: "",
  about:
    "Computer Science student focused on building practical projects. I enjoy clean backend architecture and simple, user-centered interfaces.",
  location: "Belo Horizonte, BR",
  github: "JoaoCominii",
  links: [
    { label: "LinkedIn", url: "https://www.linkedin.com" },
    { label: "GitHub", url: "https://github.com/JoaoCominii" }
  ],
  stack: {
    mastered: ["Java", "Python", "SQL", "Git"],
    learning: ["React", "Spring Boot", "Docker"]
  },
  languages: [
    { name: "Português", level: "Nativo" },
    { name: "Inglês", level: "Avançado" }
  ]
};

const ProjectCard = ({ repo }) => {
  const [imgError, setImgError] = useState(false);

  return (
    <article className="card">
      <div className="card-media">
        {!imgError && repo.imageUrl ? (
          <img
            src={repo.imageUrl}
            alt={`Preview do ${repo.name}`}
            onError={() => setImgError(true)}
          />
        ) : (
          <div className="media-fallback">
            <Github size={32} />
            <span>{repo.language || "Code"}</span>
          </div>
        )}
      </div>
      <div className="card-body">
        <h3>{repo.name}</h3>
        <p>{repo.description || "Descricao em construcao."}</p>
        <div className="card-links">
          <a className="btn ghost" href={repo.htmlUrl}>
            <Github size={16} />
            <span>GitHub</span>
          </a>
          {repo.homepage && (
            <a className="btn" href={repo.homepage}>
              <ExternalLink size={16} />
              <span>Live</span>
            </a>
          )}
        </div>
      </div>
    </article>
  );
};

export default function App() {
  const [profile, setProfile] = useState(fallbackProfile);
  const [repos, setRepos] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const controller = new AbortController();

    async function loadData() {
      try {
        const [profileRes, reposRes] = await Promise.all([
          axios.get(`${API_BASE}/api/profile`, { signal: controller.signal }),
          axios.get(`${API_BASE}/api/repos`, { signal: controller.signal })
        ]);
        setProfile(profileRes.data);
        setRepos(reposRes.data);
      } catch (error) {
        if (!axios.isCancel(error)) {
          console.warn("API offline or unavailable, using fallback data.");
        }
      } finally {
        setLoading(false);
      }
    }

    loadData();
    return () => controller.abort();
  }, []);

  const primaryLinks = useMemo(() => {
    return profile.links?.slice(0, 2) ?? [];
  }, [profile.links]);

  return (
    <div className="page">
      <div className="grid-bg" aria-hidden="true" />
      <main className="content">
        <header className="hero">
          <div className="avatar">
            {profile.photoUrl ? (
              <img src={profile.photoUrl} alt={`Foto de ${profile.name}`} />
            ) : (
              <span className="avatar-fallback">
                {profile.name?.slice(0, 1) || "?"}
              </span>
            )}
          </div>
          <div>
            <p className="eyebrow">{profile.location}</p>
            <h1 className="hero-title">
              {profile.name}. <span>{profile.title}</span>
            </h1>
            <p className="hero-subtitle">{profile.blurb}</p>
            <div className="hero-links">
              {primaryLinks.map((link) => (
                <a key={link.label} href={link.url} className="btn">
                  {link.label === "GitHub" ? (
                    <Github size={16} />
                  ) : (
                    <Linkedin size={16} />
                  )}
                  <span>{link.label}</span>
                </a>
              ))}
            </div>
          </div>
        </header>

        <section className="section">
          <div className="section-title">Sobre mim</div>
          <p className="about-text">
            {profile.about ||
              "Gosto de transformar ideias em projetos reais e aprender novas tecnologias no processo."}
          </p>
        </section>

        <section className="section">
          <div className="section-title">Stack Tecnologico</div>
          <div className="stack-grid">
            <div>
              <p className="stack-label">Dominadas</p>
              <div className="chip-row">
                {profile.stack?.mastered?.map((item) => (
                  <span key={item} className="chip">
                    {item}
                  </span>
                ))}
              </div>
            </div>
            <div>
              <p className="stack-label">Em estudo</p>
              <div className="chip-row">
                {profile.stack?.learning?.map((item) => (
                  <span key={item} className="chip chip-outline">
                    {item}
                  </span>
                ))}
              </div>
            </div>
          </div>
        </section>

        <section className="section">
          <div className="section-title">Idiomas</div>
          <div className="grid-list">
            {profile.languages?.map((lang) => (
              <div key={lang.name} className="list-item">
                <span className="lang-name">{lang.name}</span>
                <span className="lang-level">{lang.level}</span>
              </div>
            ))}
          </div>
        </section>

        <section className="section">
          <div className="section-title">Projetos em Destaque</div>
          {loading ? (
            <div className="loading">
              <Loader2 className="spin" size={18} />
              <span>Carregando repositorios...</span>
            </div>
          ) : (
            <div className="project-grid">
              {repos.map((repo) => (
                <ProjectCard key={repo.id} repo={repo} />
              ))}
            </div>
          )}
        </section>

        <footer className="footer">
          <div className="footer-links">
            {profile.links?.map((link) => (
              <a key={link.label} href={link.url}>
                {link.label}
              </a>
            ))}
          </div>
          <p>Feito com cafe e codigo.</p>
        </footer>
      </main>
    </div>
  );
}
