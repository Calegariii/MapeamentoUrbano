package controller;

import java.time.LocalDateTime;
import java.util.List;

import model.Administrador;
import model.Bairro;
import model.Cidadao;
import model.OcorrenciaSeguranca;
import model.PontoDeInteresse;
import model.Usuario;
import model.dao.BairroDAO;
import model.dao.OcorrenciaSegurancaDAO;
import model.dao.PontoDeInteresseDAO;
import model.dao.UsuarioDAO;
import util.ConexaoBD;
import util.Validacao;
import view.LoginView;

/**
 * Controller central do padrão MVC: recebe as ações das Views, aplica as
 * regras de negócio e delega a persistência aos DAOs.
 */
public class SistemaController {

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();
    private final BairroDAO bairroDAO = new BairroDAO();
    private final PontoDeInteresseDAO pontoDAO = new PontoDeInteresseDAO();
    private final OcorrenciaSegurancaDAO ocorrenciaDAO = new OcorrenciaSegurancaDAO();

    private Usuario usuarioLogado;

    /** Ponto de entrada da aplicação. */
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(() -> new LoginView(new SistemaController()).setVisible(true));
    }

    // ------------------------------------------------------------------
    // Autenticação
    // ------------------------------------------------------------------
    public Usuario login(String email, String senha) throws Exception {
        String emailValidado = Validacao.validarEmail(email);
        String senhaValidada = Validacao.textoObrigatorio(senha, "senha");
        Usuario u = usuarioDAO.autenticar(emailValidado, senhaValidada);
        if (u == null) {
            throw new Exception("E-mail ou senha incorretos. Verifique os dados informados.");
        }
        this.usuarioLogado = u;
        return u;
    }

    public Usuario getUsuarioLogado() {
        return usuarioLogado;
    }

    public boolean isAdministrador() {
        return usuarioLogado instanceof Administrador;
    }

    public void logout() {
        this.usuarioLogado = null;
    }

    public String statusConexao() {
        return ConexaoBD.testarConexao();
    }

    // ------------------------------------------------------------------
    // Bairro
    // ------------------------------------------------------------------
    public List<Bairro> listarBairros() throws Exception {
        return bairroDAO.listarTodos();
    }

    public Bairro inserirBairro(String nome, int populacao, double area) throws Exception {
        Validacao.textoObrigatorio(nome, "nome do bairro");
        Validacao.validarNaoNegativo(populacao, "população estimada");
        Validacao.validarPositivo(area, "área (km²)");
        return bairroDAO.inserir(new Bairro(nome, populacao, area));
    }

    // ------------------------------------------------------------------
    // Pontos de interesse
    // ------------------------------------------------------------------
    public List<PontoDeInteresse> listarPontos(String filtroTipo) throws Exception {
        return pontoDAO.listar(filtroTipo == null || filtroTipo.isEmpty() ? null : filtroTipo);
    }

    public PontoDeInteresse buscarPonto(Long id) throws Exception {
        return pontoDAO.buscarPorId(id);
    }

    public void salvarPonto(PontoDeInteresse ponto, boolean novo) throws Exception {
        validarPonto(ponto);
        if (novo) {
            pontoDAO.inserir(ponto);
        } else {
            pontoDAO.atualizar(ponto);
        }
    }

    public void excluirPonto(Long id) throws Exception {
        if (id == null) {
            throw new Exception("Selecione um ponto na tabela antes de excluir.");
        }
        pontoDAO.excluir(id);
    }

    private void validarPonto(PontoDeInteresse p) throws Exception {
        Validacao.textoObrigatorio(p.getNome(), "nome");
        Validacao.textoObrigatorio(p.getEndereco(), "endereço");
        Validacao.validarLatitude(p.getLatitude());
        Validacao.validarLongitude(p.getLongitude());
        if (p.getBairro() == null || p.getBairro().getIdBairro() == null) {
            throw new Exception("Selecione um bairro válido.");
        }
        if (p.getTipo() == null || p.getTipo().trim().isEmpty()) {
            throw new Exception("Informe o tipo do ponto de interesse.");
        }
    }

    // ------------------------------------------------------------------
    // Ocorrências de segurança
    // ------------------------------------------------------------------
    public List<Cidadao> listarCidadaos() throws Exception {
        return usuarioDAO.listarCidadaos();
    }

    public List<OcorrenciaSeguranca> listarOcorrencias(Long idBairro) throws Exception {
        return idBairro == null ? ocorrenciaDAO.listarTodas()
                : ocorrenciaDAO.listarPorBairro(idBairro);
    }

    public void registrarOcorrencia(Cidadao cidadao, Bairro bairro, String tipoEvento,
                                    String gravidade) throws Exception {
        if (cidadao == null || cidadao.getIdUsuario() == null) {
            throw new Exception("Selecione o cidadão registrante.");
        }
        if (bairro == null || bairro.getIdBairro() == null) {
            throw new Exception("Selecione o bairro da ocorrência.");
        }
        Validacao.textoObrigatorio(tipoEvento, "tipo de evento");
        if (gravidade == null || !gravidade.matches("BAIXA|MEDIA|ALTA")) {
            throw new Exception("Selecione um nível de gravidade válido.");
        }
        // RN02: o sistema não solicita identificação de vítimas nem endereço residencial.
        OcorrenciaSeguranca o = new OcorrenciaSeguranca(cidadao, bairro, tipoEvento,
                LocalDateTime.now(), gravidade, false);
        ocorrenciaDAO.inserir(o);
    }

    public void alterarStatus(Long idOcorrencia, boolean aprovado) throws Exception {
        if (!isAdministrador()) {
            throw new Exception("Apenas o Administrador pode moderar ocorrências.");
        }
        if (idOcorrencia == null) {
            throw new Exception("Selecione uma ocorrência na tabela.");
        }
        ocorrenciaDAO.atualizarStatus(idOcorrencia, aprovado);
    }
}
