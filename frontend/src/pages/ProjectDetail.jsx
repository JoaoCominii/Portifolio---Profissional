import { useEffect, useState } from "react";
import { useParams, Link } from "react-router-dom";
import axios from "axios";
import { ArrowLeft, Github, Loader2, Play } from "lucide-react";

const API_BASE = import.meta.env.VITE_API_BASE || "http://localhost:8081";

export default function ProjectDetail() {
  const { id } = useParams();
  const [project, setProject] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(false);
  const [activeMediaIndex, setActiveMediaIndex] = useState(0);

  useEffect(() => {
    const controller = new AbortController();
    setLoading(true);
    setError(false);
    setActiveMediaIndex(0);

    axios
      .get(`${API_BASE}/api/project/${id}`, { signal: controller.signal })
      .then((res) => setProject(res.data))
      .catch((err) => {
        if (!axios.isCancel(err)) setError(true);
      })
      .finally(() => setLoading(false));

    return () => controller.abort();
  }, [id]);

  if (loading) {
    return (
      <div className="page">
        <main className="content content-detail">
          <div className="loading">
            <Loader2 className="spin" size={18} />
            <span>Carregando projeto...</span>
          </div>
        </main>
      </div>
    );
  }

  if (error || !project) {
    return (
      <div className="page">
        <main className="content content-detail">
          <Link to="/" className="back-link">
            <ArrowLeft size={16} />
            <span>Voltar para a home</span>
          </Link>
          <p className="about-text">Projeto não encontrado.</p>
        </main>
      </div>
    );
  }

  const tags = (project.tags || "")
    .split(",")
    .map((t) => t.trim())
    .filter(Boolean);

  const media = project.media || [];
  const activeMedia = media[activeMediaIndex];

  return (
    <div className="page">
      <div className="grid-bg" aria-hidden="true" />
      <main className="content content-detail">
        <Link to="/" className="back-link">
          <ArrowLeft size={16} />
          <span>Voltar para a home</span>
        </Link>

        <header className="detail-header">
          <h1 className="detail-title">{project.name}</h1>
          <p className="eyebrow">{project.tagline}</p>
        </header>

        {media.length > 0 && (
          <div className="media-gallery">
            <div className="media-main">
              {activeMedia?.type === "video" ? (
                <video
                  className="detail-media"
                  src={activeMedia.url}
                  controls
                  preload="metadata"
                />
              ) : (
                <img
                  className="detail-media"
                  src={activeMedia?.url}
                  alt={`Preview do ${project.name}`}
                />
              )}
            </div>
            {media.length > 1 && (
              <div className="media-thumbnails">
                {media.map((item, index) => (
                  <button
                    key={index}
                    className={`media-thumb ${index === activeMediaIndex ? "active" : ""}`}
                    onClick={() => setActiveMediaIndex(index)}
                  >
                    {item.type === "video" ? (
                      <>
                        <img src={item.url || media[0]?.url} alt="" />
                        <div className="play-overlay">
                          <Play size={16} />
                        </div>
                      </>
                    ) : (
                      <img src={item.url} alt="" />
                    )}
                  </button>
                ))}
              </div>
            )}
          </div>
        )}

        {media.length === 0 && (
          <div className="media-placeholder">
            <p>Sem mídia disponível</p>
          </div>
        )}

        <div className="detail-body">
          <section className="detail-section">
            <div className="section-title">Sobre o projeto</div>
            <p className="about-text">{project.description}</p>
          </section>

          {tags.length > 0 && (
            <section className="detail-section">
              <div className="section-title">Tecnologias</div>
              <div className="chip-row">
                {tags.map((tag) => (
                  <span key={tag} className="chip">
                    {tag}
                  </span>
                ))}
              </div>
            </section>
          )}

          <section className="detail-section detail-actions">
            {project.sourceUrl && project.sourceUrl !== "Privado" && (
              <a className="btn" href={project.sourceUrl} target="_blank" rel="noreferrer">
                <Github size={16} />
                <span>Código fonte</span>
              </a>
            )}
            {(!project.sourceUrl || project.sourceUrl === "Privado") && project.projectUrl === "Privado" && (
              <span className="private-badge">
                <Github size={16} />
                <span>Repositório privado</span>
              </span>
            )}
          </section>
        </div>

        <footer className="footer">
          <p>Feito com cafe e codigo.</p>
        </footer>
      </main>
    </div>
  );
}
