const API_BASE = location.port === '8080' ? '' : 'http://localhost:8080';
const state = { token: localStorage.getItem('token'), user: JSON.parse(localStorage.getItem('user') || 'null'), bairros: [], pontos: [], ocorrencias: [], cidadaos: [] };
let map, markersLayer;

function ensureMap() {
  if (map || typeof L === 'undefined') return;
  map = L.map('map').setView([-27.645, -48.667], 12);
  L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', { maxZoom: 19, attribution: '&copy; OpenStreetMap' }).addTo(map);
  markersLayer = L.layerGroup().addTo(map);
}

async function renderMapMarkers() {
  if (!map) return;
  const filtro = $('filtro-mapa')?.value || '';
  const showPontos = filtro !== 'OCORRENCIA';
  const showOcorr = filtro === '' || filtro === 'OCORRENCIA';
  markersLayer.clearLayers();

  if (showPontos) {
    const pontos = (filtro === 'SAUDE' || filtro === 'EDUCACAO') ? state.pontos.filter(p => p.categoria === filtro) : state.pontos;
    pontos.forEach(p => {
      const color = p.categoria === 'SAUDE' ? '#14b8a6' : '#f59e0b';
      const icon = L.divIcon({ className: '', html: `<div style="background:${color};width:14px;height:14px;border-radius:50%;border:2px solid #fff;box-shadow:0 0 0 2px rgba(0,0,0,.25)"></div>`, iconSize: [14,14], iconAnchor: [7,7] });
      L.marker([p.latitude, p.longitude], { icon }).addTo(markersLayer)
        .bindPopup(`<strong>${p.nome}</strong><br>${p.tipo} • ${p.categoria === 'SAUDE' ? 'Saúde' : 'Educação'}<br>${p.bairro?.nome || ''}<br><span style="color:#94a3b8">${p.endereco}</span><br><small>${p.coordenadas}</small>`);
    });
  }

  if (showOcorr) {
    state.ocorrencias.filter(o => o.latitude != null && o.longitude != null).forEach(o => {
      const cor = o.nivelGravidade === 'ALTA' ? '#ef4444' : o.nivelGravidade === 'MEDIA' ? '#f59e0b' : '#22c55e';
      const icon = L.divIcon({ className: '', html: `<div style="background:${cor};width:14px;height:14px;border-radius:50%;border:2px solid #fff;box-shadow:0 0 0 2px rgba(0,0,0,.25)"></div>`, iconSize: [14,14], iconAnchor: [7,7] });
      L.marker([o.latitude, o.longitude], { icon }).addTo(markersLayer)
        .bindPopup(`<strong>Ocorrência: ${o.tipoEvento}</strong><br>${o.bairro?.nome || ''} • ${o.nivelGravidade}<br>Status: ${o.statusAprovacao ? 'Aprovada' : 'Pendente'}<br><small>${new Date(o.dataHora).toLocaleString('pt-BR')}</small>`);
    });
  }

  const layers = markersLayer.getLayers();
  if (layers.length) map.fitBounds(L.featureGroup(layers).getBounds().pad(0.2));
}

const $ = (id) => document.getElementById(id);
const fmt = (n) => Number(n).toLocaleString('pt-BR');
const toast = (msg, ok = true) => { const t = $('toast'); t.textContent = msg; t.className = `toast ${ok ? 'ok' : 'err'}`; setTimeout(() => t.classList.add('hidden'), 3500); };

async function api(path, opts = {}) {
  const headers = { 'Content-Type': 'application/json', ...(opts.headers || {}) };
  if (state.token) headers.Authorization = `Bearer ${state.token}`;
  const res = await fetch(API_BASE + path, { ...opts, headers });
  const data = await res.json().catch(() => ({}));
  if (!res.ok) throw new Error(data.erro || 'Erro na requisição');
  return data;
}

function showView(id) { document.querySelectorAll('.view').forEach(v => v.classList.remove('active')); $(id).classList.add('active'); }
window.showTab = function(tab) {
  document.querySelectorAll('.nav-item').forEach(b => b.classList.toggle('active', b.dataset.tab === tab));
  document.querySelectorAll('.tab').forEach(t => t.classList.toggle('active', t.id === `tab-${tab}`));
  const titles = { dashboard: ['Dashboard', 'Visão geral da plataforma urbana de Palhoça.'], mapa: ['Mapa Interativo', 'Visualização geográfica dos pontos de interesse em Palhoça.'], pontos: ['Pontos de Interesse', 'Cadastro, filtro e manutenção dos pontos reais.'], bairros: ['Bairros', 'Cadastro e consulta das localidades de Palhoça.'], ocorrencias: ['Ocorrências', 'Registro e moderação de ocorrências por bairro.'] };
  $('page-title').textContent = titles[tab][0]; $('page-sub').textContent = titles[tab][1];
  if (tab === 'mapa') { ensureMap(); loadOcorrencias().catch(()=>{}); setTimeout(() => { map.invalidateSize(); renderMapMarkers(); }, 100); }
  if (tab === 'pontos') loadPontos();
  if (tab === 'bairros') loadBairros();
  if (tab === 'ocorrencias') { loadCidadaos(); loadOcorrencias(); }
};

async function login() {
  try {
    const data = await api('/api/login', { method: 'POST', body: JSON.stringify({ email: $('login-email').value, senha: $('login-senha').value }) });
    state.token = data.token; state.user = data.usuario;
    localStorage.setItem('token', data.token); localStorage.setItem('user', JSON.stringify(data.usuario));
    toast(data.mensagem || 'Login realizado');
    enterApp();
  } catch (e) { $('login-msg').textContent = e.message; toast(e.message, false); }
}

function logout() { state.token = null; state.user = null; localStorage.removeItem('token'); localStorage.removeItem('user'); showView('view-login'); }

async function enterApp() {
  if (!state.token) return showView('view-login');
  showView('view-app');
  $('user-info').innerHTML = `<strong>${state.user.nome}</strong>${state.user.perfil}<br>${state.user.email}`;
  await Promise.all([loadBairros(), loadPontos(), loadOcorrencias(), checkStatus()]);
  renderDashboard();
}

async function checkStatus() {
  try { const s = await api('/api/status'); $('db-status').textContent = 'Banco: conectado'; $('db-status').style.color = '#5eead4'; $('login-msg').textContent = s.status; toast(s.status, true); }
  catch (e) { $('db-status').textContent = 'Banco: erro'; $('db-status').style.color = '#fca5a5'; $('login-msg').textContent = e.message; toast(e.message, false); }
}

async function loadBairros() {
  state.bairros = await api('/api/bairros');
  $('count-bairros').textContent = state.bairros.length;
  $('d-bairros').textContent = state.bairros.length;
  $('stat-bairros').textContent = state.bairros.length;
  const rows = state.bairros.map(b => `<tr><td>${b.idBairro}</td><td>${b.nome}</td><td>${fmt(b.populacaoEstimada)}</td><td>${Number(b.areaKm2).toFixed(2)}</td></tr>`).join('');
  $('tbody-bairros').innerHTML = rows || '<tr><td colspan="4" class="muted">Nenhum bairro.</td></tr>';
  const opts = state.bairros.map(b => `<option value="${b.idBairro}">${b.nome}</option>`).join('');
  $('p-bairro').innerHTML = opts;
  $('o-bairro').innerHTML = opts;
  $('filtro-bairro-oc').innerHTML = '<option value="">Todos os bairros</option>' + opts;
}

async function loadPontos() {
  const tipo = $('filtro-tipo').value;
  state.pontos = await api('/api/pontos' + (tipo ? `?tipo=${encodeURIComponent(tipo)}` : ''));
  $('d-pontos').textContent = state.pontos.length;
  $('stat-pontos').textContent = state.pontos.length;
  $('tbody-pontos').innerHTML = state.pontos.map(p => `
    <tr>
      <td>${p.idPonto}</td><td>${p.nome}</td>
      <td><span class="badge ${p.categoria === 'SAUDE' ? 'info' : 'ok'}">${p.categoria === 'SAUDE' ? 'Saúde' : 'Educação'}</span></td>
      <td>${p.tipo}</td><td>${p.bairro.nome}</td><td>${p.endereco}</td><td>${p.coordenadas}</td>
      <td class="actions"><button class="btn ghost small-btn" onclick='editPonto(${p.idPonto})'>Editar</button><button class="btn danger small-btn" onclick='delPonto(${p.idPonto},"${p.nome.replace(/"/g,'&quot;')}")'>Excluir</button></td>
    </tr>`).join('') || '<tr><td colspan="8" class="muted">Nenhum ponto encontrado.</td></tr>';
  renderDashboard();
  renderMapMarkers();
}

async function loadCidadaos() {
  if (state.cidadaos.length) return;
  try { state.cidadaos = await api('/api/cidadaos'); $('o-cidadao').innerHTML = state.cidadaos.map(c => `<option value="${c.idUsuario}">${c.nome}</option>`).join(''); } catch {}
}

async function loadOcorrencias() {
  const b = $('filtro-bairro-oc').value;
  state.ocorrencias = await api('/api/ocorrencias' + (b ? `?bairroId=${b}` : ''));
  $('d-ocorrencias').textContent = state.ocorrencias.length;
  $('d-pendentes').textContent = state.ocorrencias.filter(o => !o.statusAprovacao).length;
  const isAdmin = state.user?.perfil === 'Administrador';
  $('tbody-ocorrencias').innerHTML = state.ocorrencias.map(o => `
    <tr>
      <td>${o.idOcorrencia}</td><td>${o.bairro.nome}</td><td>${o.tipoEvento}</td>
      <td><span class="badge ${o.nivelGravidade === 'ALTA' ? 'danger' : o.nivelGravidade === 'MEDIA' ? 'warn' : 'ok'}">${o.nivelGravidade}</span></td>
      <td><span class="badge ${o.statusAprovacao ? 'ok' : 'warn'}">${o.statusAprovacao ? 'Aprovada' : 'Pendente'}</span></td>
      <td>${isAdmin ? `<div class="actions"><button class="btn ghost small-btn" onclick="moderar(${o.idOcorrencia},true)">Aprovar</button><button class="btn danger small-btn" onclick="moderar(${o.idOcorrencia},false)">Reprovar</button></div>` : '<span class="muted">—</span>'}</td>
    </tr>`).join('') || '<tr><td colspan="6" class="muted">Nenhuma ocorrência.</td></tr>';
  renderDashboard();
  renderMapMarkers();
}

function renderDashboard() {
  $('dash-pontos').innerHTML = state.pontos.slice(0, 5).map(p => `<tr><td>${p.nome}</td><td>${p.categoria === 'SAUDE' ? 'Saúde' : 'Educação'}</td><td>${p.bairro.nome}</td></tr>`).join('') || '<tr><td colspan="3" class="muted">Sem dados.</td></tr>';
  $('dash-ocorrencias').innerHTML = state.ocorrencias.slice(0, 5).map(o => `<tr><td>${o.tipoEvento}</td><td>${o.bairro.nome}</td><td><span class="badge ${o.nivelGravidade === 'ALTA' ? 'danger' : o.nivelGravidade === 'MEDIA' ? 'warn' : 'ok'}">${o.nivelGravidade}</span></td></tr>`).join('') || '<tr><td colspan="3" class="muted">Sem dados.</td></tr>';
}

function openPontoModal(p = null) {
  $('modal-ponto-title').textContent = p ? 'Editar Ponto' : 'Novo Ponto';
  $('p-id').value = p?.idPonto || '';
  $('p-nome').value = p?.nome || '';
  $('p-categoria').value = p?.categoria || 'SAUDE';
  $('p-endereco').value = p?.endereco || '';
  $('p-bairro').value = p?.bairro?.idBairro || state.bairros[0]?.idBairro || '';
  $('p-lat').value = p?.latitude ?? -27.6500000;
  $('p-lng').value = p?.longitude ?? -48.6700000;
  $('p-especialidades').value = p?.especialidades || '';
  $('p-horario').value = p?.horarioFuncionamento || '';
  $('p-esfera').value = p?.esfera || 'Pública';
  $('p-niveis').value = p?.niveisAtendidos || '';
  $('p-24h').checked = !!p?.possuiAtendimento24h;
  $('p-vagas').checked = !!p?.possuiVagasAbertas;
  syncCategoria(p?.tipo);
  $('modal-ponto').classList.remove('hidden');
}
window.closeModal = () => $('modal-ponto').classList.add('hidden');

function syncCategoria(selectedTipo) {
  const cat = $('p-categoria').value;
  const saude = cat === 'SAUDE';
  $('campo-saude-esp').classList.toggle('hidden', !saude);
  $('campo-saude-hor').classList.toggle('hidden', !saude);
  $('campo-saude-24h').classList.toggle('hidden', !saude);
  $('campo-edu-esfera').classList.toggle('hidden', saude);
  $('campo-edu-niveis').classList.toggle('hidden', saude);
  $('campo-edu-vagas').classList.toggle('hidden', saude);
  $('p-tipo').innerHTML = (saude ? ['UBS','UPA','Hospital','Pronto-Socorro'] : ['Escola Municipal','Escola Estadual','Universidade','Faculdade','CEI'])
    .map(t => `<option ${t===selectedTipo?'selected':''}>${t}</option>`).join('');
}

window.editPonto = async (id) => { try { openPontoModal(await api(`/api/pontos/${id}`)); } catch(e){ toast(e.message,false); } };
window.delPonto = async (id, nome) => {
  if (!confirm(`Confirma a exclusão de "${nome}"?`)) return;
  try { await api(`/api/pontos/${id}`, { method: 'DELETE' }); toast('Ponto excluído.'); loadPontos(); } catch(e){ toast(e.message,false); }
};

async function savePonto() {
  const id = $('p-id').value;
  const cat = $('p-categoria').value;
  const body = {
    nome: $('p-nome').value, categoria: cat, endereco: $('p-endereco').value,
    idBairro: Number($('p-bairro').value), tipo: $('p-tipo').value,
    latitude: Number($('p-lat').value), longitude: Number($('p-lng').value),
    especialidades: $('p-especialidades').value, horarioFuncionamento: $('p-horario').value,
    possuiAtendimento24h: $('p-24h').checked, esfera: $('p-esfera').value,
    niveisAtendidos: $('p-niveis').value, possuiVagasAbertas: $('p-vagas').checked
  };
  try {
    await api(id ? `/api/pontos/${id}` : '/api/pontos', { method: id ? 'PUT' : 'POST', body: JSON.stringify(body) });
    toast(id ? 'Ponto atualizado.' : 'Ponto cadastrado.'); closeModal(); loadPontos();
  } catch(e) { toast(e.message, false); }
}

async function saveBairro() {
  try {
    await api('/api/bairros', { method: 'POST', body: JSON.stringify({ nome: $('b-nome').value, populacaoEstimada: Number($('b-pop').value), areaKm2: Number($('b-area').value) }) });
    $('b-nome').value = ''; $('b-pop').value = 0; $('b-area').value = 1;
    toast('Bairro cadastrado.'); loadBairros();
  } catch(e) { toast(e.message, false); }
}

async function saveOcorrencia() {
  try {
    await api('/api/ocorrencias', { method: 'POST', body: JSON.stringify({ idBairro: Number($('o-bairro').value), idCidadao: Number($('o-cidadao').value), tipoEvento: $('o-tipo').value, nivelGravidade: $('o-gravidade').value, latitude: Number($('o-lat').value), longitude: Number($('o-lng').value) }) });
    $('o-tipo').value = ''; toast('Ocorrência registrada. Aguardando aprovação do Administrador.'); loadOcorrencias();
  } catch(e) { toast(e.message, false); }
}

window.moderar = async (id, aprovado) => {
  try { await api('/api/ocorrencias', { method: 'PATCH', body: JSON.stringify({ idOcorrencia: id, statusAprovacao: aprovado }) }); toast(aprovado ? 'Ocorrência aprovada.' : 'Ocorrência reprovada.'); loadOcorrencias(); }
  catch(e) { toast(e.message, false); }
};

document.querySelectorAll('.nav-item').forEach(b => b.addEventListener('click', () => showTab(b.dataset.tab)));
$('btn-login').addEventListener('click', login);
$('login-senha').addEventListener('keydown', e => e.key === 'Enter' && login());
$('btn-logout').addEventListener('click', logout);
$('btn-status').addEventListener('click', checkStatus);
$('filtro-tipo').addEventListener('change', loadPontos);
$('btn-novo-ponto').addEventListener('click', () => openPontoModal());
$('btn-salvar-ponto').addEventListener('click', savePonto);
$('p-categoria').addEventListener('change', () => syncCategoria());
$('btn-salvar-bairro').addEventListener('click', saveBairro);
$('btn-salvar-ocorrencia').addEventListener('click', saveOcorrencia);
$('filtro-bairro-oc').addEventListener('change', loadOcorrencias);
$('filtro-mapa')?.addEventListener('change', renderMapMarkers);
$('btn-recarregar-mapa')?.addEventListener('click', async () => { await loadPontos(); renderMapMarkers(); toast('Mapa atualizado.'); });

if (state.token && state.user) enterApp(); else showView('view-login');
