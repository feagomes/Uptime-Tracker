import { useEffect, useState } from "react";
import {
  getServices,
  getServiceHistory,
  getServiceStats,
  createService,
} from "./services/api";
import {
  LineChart,
  Line,
  XAxis,
  YAxis,
  Tooltip,
  ResponsiveContainer,
} from "recharts";
import { Activity, Plus } from "lucide-react";

export default function App() {
  const [services, setServices] = useState([]);
  const [selectedService, setSelectedService] = useState(null);
  const [history, setHistory] = useState([]);
  const [stats, setStats] = useState(null);
  const [newName, setNewName] = useState("");
  const [newUrl, setNewUrl] = useState("");

  useEffect(() => {
    const fetchServicesList = async () => {
      try {
        const { data } = await getServices();
        setServices(data);

        // Auto-seleciona o primeiro da lista apenas se não houver nenhum selecionado
        setSelectedService((current) => {
          if (!current && data.length > 0) return data[0];
          return current;
        });
      } catch (err) {
        console.error("Erro ao carregar serviços:", err);
      }
    };

    fetchServicesList();
    const interval = setInterval(fetchServicesList, 15000);
    return () => clearInterval(interval);
  }, []); // Sem dependências externas = sem avisos do linter

  // Efeito 2: Busca os gráficos/histórico APENAS do serviço selecionado (a cada 15s)
  useEffect(() => {
    if (!selectedService) return;

    const fetchServiceDetails = async () => {
      try {
        const [historyRes, statsRes] = await Promise.all([
          getServiceHistory(selectedService.id),
          getServiceStats(selectedService.id),
        ]);
        setHistory([...historyRes.data].reverse());
        setStats(statsRes.data);
      } catch (err) {
        console.error("Erro ao carregar detalhes:", err);
      }
    };

    fetchServiceDetails();
    const interval = setInterval(fetchServiceDetails, 9000);
    return () => clearInterval(interval);
  }, [selectedService]);

  const handleAddService = async (e) => {
    e.preventDefault();
    if (!newName || !newUrl) return;

    await createService({ name: newName, url: newUrl });
    setNewName("");
    setNewUrl("");

    // Atualiza a lista lateral após cadastrar
    const { data } = await getServices();
    setServices(data);
  };

  return (
    <div
      style={{
        fontFamily: "sans-serif",
        padding: "2rem",
        backgroundColor: "#0f172a",
        color: "#f8fafc",
        minHeight: "100vh",
      }}
    >
      <header
        style={{
          display: "flex",
          alignItems: "center",
          gap: "10px",
          marginBottom: "2rem",
          borderBottom: "1px solid #334155",
          paddingBottom: "1rem",
        }}
      >
        <Activity color="#38bdf8" size={32} />
        <h1 style={{ margin: 0, fontSize: "1.5rem" }}>
          Uptime & Performance Tracker
        </h1>
      </header>

      <form
        onSubmit={handleAddService}
        style={{
          display: "flex",
          gap: "10px",
          marginBottom: "2rem",
          background: "#1e293b",
          padding: "1rem",
          borderRadius: "8px",
        }}
      >
        <input
          placeholder="Nome (ex: API Pix)"
          value={newName}
          onChange={(e) => setNewName(e.target.value)}
          style={inputStyle}
        />
        <input
          placeholder="URL (https://...)"
          value={newUrl}
          onChange={(e) => setNewUrl(e.target.value)}
          style={inputStyle}
        />
        <button type="submit" style={btnStyle}>
          <Plus size={16} /> Adicionar Target
        </button>
      </form>

      <div
        style={{
          display: "grid",
          gridTemplateColumns: "300px 1fr",
          gap: "20px",
        }}
      >
        <div
          style={{
            background: "#1e293b",
            padding: "1rem",
            borderRadius: "8px",
          }}
        >
          <h2 style={{ fontSize: "1.1rem", marginBottom: "1rem" }}>
            Serviços ({services.length})
          </h2>
          {services.map((s) => (
            <div
              key={s.id}
              onClick={() => setSelectedService(s)}
              style={{
                padding: "12px",
                marginBottom: "8px",
                borderRadius: "6px",
                cursor: "pointer",
                background:
                  selectedService?.id === s.id ? "#334155" : "#0f172a",
                borderLeft:
                  selectedService?.id === s.id ? "4px solid #38bdf8" : "none",
              }}
            >
              <div style={{ fontWeight: "bold" }}>{s.name}</div>
              <div style={{ fontSize: "0.8rem", color: "#94a3b8" }}>
                {s.url}
              </div>
            </div>
          ))}
        </div>
        {selectedService && (
          <div
            style={{
              background: "#1e293b",
              padding: "1.5rem",
              borderRadius: "8px",
            }}
          >
            <div
              style={{
                display: "flex",
                justifyContent: "space-between",
                alignItems: "center",
                marginBottom: "1.5rem",
              }}
            >
              <div>
                <h2 style={{ margin: 0 }}>{selectedService.name}</h2>
                <span style={{ fontSize: "0.85rem", color: "#94a3b8" }}>
                  {selectedService.url}
                </span>
              </div>
              {stats && (
                <div style={{ display: "flex", gap: "15px" }}>
                  <div style={{ textAlign: "right" }}>
                    <div style={{ fontSize: "0.8rem", color: "#94a3b8" }}>
                      Disponibilidade
                    </div>
                    <div
                      style={{
                        fontSize: "1.2rem",
                        fontWeight: "bold",
                        color: "#4ade80",
                      }}
                    >
                      {stats.uptimePercentage}%
                    </div>
                  </div>
                  <div style={{ textAlign: "right" }}>
                    <div style={{ fontSize: "0.8rem", color: "#94a3b8" }}>
                      Status
                    </div>
                    <div
                      style={{
                        fontSize: "1.2rem",
                        fontWeight: "bold",
                        color:
                          stats.status === "ONLINE" ? "#4ade80" : "#f87171",
                      }}
                    >
                      {stats.status}
                    </div>
                  </div>
                </div>
              )}
            </div>

            <h3>Latência em Tempo Real (ms)</h3>
            <div style={{ width: "100%", height: 300, marginTop: "1rem" }}>
              <ResponsiveContainer width="100%" height="100%">
                <LineChart data={history}>
                  <XAxis
                    dataKey="timestamp"
                    tick={{ fill: "#94a3b8", fontSize: 10 }}
                    tickFormatter={(t) => new Date(t).toLocaleTimeString()}
                  />
                  <YAxis tick={{ fill: "#94a3b8" }} />
                  <Tooltip
                    contentStyle={{
                      backgroundColor: "#0f172a",
                      borderColor: "#334155",
                      color: "#fff",
                    }}
                  />
                  <Line
                    type="monotone"
                    dataKey="responseTimeMs"
                    stroke="#38bdf8"
                    strokeWidth={2}
                    dot={false}
                  />
                </LineChart>
              </ResponsiveContainer>
            </div>
          </div>
        )}
      </div>
    </div>
  );
}

const inputStyle = {
  padding: "8px 12px",
  background: "#0f172a",
  border: "1px solid #334155",
  borderRadius: "4px",
  color: "#fff",
  flex: 1,
};
const btnStyle = {
  padding: "8px 16px",
  background: "#0284c7",
  border: "none",
  borderRadius: "4px",
  color: "#fff",
  cursor: "pointer",
  display: "flex",
  alignItems: "center",
  gap: "6px",
};
