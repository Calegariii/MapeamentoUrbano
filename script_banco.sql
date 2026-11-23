-- =====================================================================
-- PLATAFORMA DE MAPEAMENTO E VISUALIZACAO DE DADOS URBANOS - PALHOCA/SC
-- Projeto Integrador / AV2 - Pratica de Linguagem de Programacao I - Java POO
-- Banco: PostgreSQL | Usuario: admin | Senha: admin
--
-- Fontes dos dados dos pontos de interesse:
--  * Prefeitura Municipal de Palhoca / Secretaria Municipal de Saude
--    http://www1.palhoca.sc.gov.br/v2carta/secretarias/saude.php
--  * Secretaria de Estado da Saude / CNES (Datasus)
--  * Secretaria de Estado da Educacao de Santa Catarina (SED)
--  * Faculdade Municipal de Palhoca (FMP) - fmpsc.edu.br
--  * Subsecretaria de Seguranca Publica / SSP-SC
--  * Coordenadas geocodificadas via OpenStreetMap (Nominatim)
--  * Bairros: registro eleitoral de Palhoca (TRE-SC) e denominacoes oficiais
--  * Populacao: estimativa IBGE 2025 (253.469 hab.) distribuida proporcionalmente
--    aos eleitores de cada bairro. Valores aproximados para fins academicos.
-- Dados de pessoas sao FICTICIOS, em conformidade com a LGPD (RN02).
-- =====================================================================

DROP TABLE IF EXISTS ocorrencia_seguranca, instituicao_ensino, unidade_saude,
    ponto_de_interesse, avaliacao_cobertura_saude, planejamento_educacional,
    bairro, cidadao, gestor_publico, administrador, usuario CASCADE;

CREATE TABLE usuario (
    id_usuario BIGSERIAL PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    senha_hash VARCHAR(255) NOT NULL,
    data_cadastro DATE NOT NULL DEFAULT CURRENT_DATE
);

CREATE TABLE cidadao (
    id_usuario BIGINT PRIMARY KEY REFERENCES usuario(id_usuario),
    cpf CHAR(11) NOT NULL UNIQUE
);

CREATE TABLE administrador (
    id_usuario BIGINT PRIMARY KEY REFERENCES usuario(id_usuario),
    nivel_acesso INTEGER NOT NULL DEFAULT 1
);

CREATE TABLE gestor_publico (
    id_usuario BIGINT PRIMARY KEY REFERENCES usuario(id_usuario),
    cargo VARCHAR(100) NOT NULL,
    orgao_vinculado VARCHAR(150) NOT NULL
);

CREATE TABLE bairro (
    id_bairro BIGSERIAL PRIMARY KEY,
    nome VARCHAR(100) NOT NULL UNIQUE,
    populacao_estimada INTEGER NOT NULL CHECK (populacao_estimada >= 0),
    area_km2 NUMERIC(10,2) NOT NULL CHECK (area_km2 > 0)
);

CREATE TABLE ponto_de_interesse (
    id_ponto BIGSERIAL PRIMARY KEY,
    nome VARCHAR(150) NOT NULL,
    latitude DOUBLE PRECISION NOT NULL CHECK (latitude BETWEEN -90 AND 90),
    longitude DOUBLE PRECISION NOT NULL CHECK (longitude BETWEEN -180 AND 180),
    endereco VARCHAR(200) NOT NULL,
    id_bairro BIGINT NOT NULL REFERENCES bairro(id_bairro),
    categoria VARCHAR(20) NOT NULL CHECK (categoria IN ('SAUDE','EDUCACAO'))
);

CREATE TABLE unidade_saude (
    id_ponto BIGINT PRIMARY KEY REFERENCES ponto_de_interesse(id_ponto),
    tipo VARCHAR(40) NOT NULL,
    especialidades VARCHAR(120) NOT NULL,
    horario_funcionamento VARCHAR(100) NOT NULL,
    possui_atendimento_24h BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE TABLE instituicao_ensino (
    id_ponto BIGINT PRIMARY KEY REFERENCES ponto_de_interesse(id_ponto),
    tipo VARCHAR(40) NOT NULL,
    esfera VARCHAR(30) NOT NULL,
    niveis_atendidos VARCHAR(100) NOT NULL,
    possui_vagas_abertas BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE TABLE ocorrencia_seguranca (
    id_ocorrencia BIGSERIAL PRIMARY KEY,
    id_usuario_cidadao BIGINT NOT NULL REFERENCES cidadao(id_usuario),
    id_bairro BIGINT NOT NULL REFERENCES bairro(id_bairro),
    tipo_evento VARCHAR(100) NOT NULL,
    data_hora TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    nivel_gravidade VARCHAR(20) NOT NULL CHECK (nivel_gravidade IN ('BAIXA','MEDIA','ALTA')),
    status_aprovacao BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE TABLE avaliacao_cobertura_saude (
    id_avaliacao BIGSERIAL PRIMARY KEY,
    id_gestor BIGINT NOT NULL REFERENCES gestor_publico(id_usuario),
    id_bairro BIGINT NOT NULL REFERENCES bairro(id_bairro),
    total_leitos_upa INTEGER NOT NULL CHECK (total_leitos_upa >= 0),
    total_equipes_ubs INTEGER NOT NULL CHECK (total_equipes_ubs >= 0),
    indice_atendimento_habitante NUMERIC(4,2) NOT NULL
);

CREATE TABLE planejamento_educacional (
    id_planejamento BIGSERIAL PRIMARY KEY,
    id_gestor BIGINT NOT NULL REFERENCES gestor_publico(id_usuario),
    id_bairro BIGINT NOT NULL REFERENCES bairro(id_bairro),
    regiao_mapeada VARCHAR(150) NOT NULL,
    demanda_estimada_alunos INTEGER NOT NULL CHECK (demanda_estimada_alunos >= 0),
    total_escolas_disponiveis INTEGER NOT NULL CHECK (total_escolas_disponiveis >= 0)
);

-- ---------------------------------------------------------------------
-- USUARIOS (dados ficticios para demonstracao)
-- ---------------------------------------------------------------------
INSERT INTO usuario (nome, email, senha_hash, data_cadastro) VALUES
('Administrador do Sistema', 'admin@palhoca.sc.gov.br', 'admin', DATE '2026-01-10'),
('Ana Souza', 'ana@cidadao.test', '123', DATE '2026-01-11'),
('Carlos Lima', 'carlos@cidadao.test', '123', DATE '2026-01-12'),
('Mariana Alves', 'mariana@cidadao.test', '123', DATE '2026-01-13'),
('Secretaria Municipal de Saude', 'saude@palhoca.sc.gov.br', 'gestor', DATE '2026-01-14'),
('Secretaria Municipal de Educacao', 'educacao@palhoca.sc.gov.br', 'gestor', DATE '2026-01-15');

INSERT INTO administrador (id_usuario, nivel_acesso) VALUES (1, 3);
INSERT INTO cidadao (id_usuario, cpf) VALUES
(2, '11144477735'), (3, '12345678909'), (4, '98765432100');
INSERT INTO gestor_publico (id_usuario, cargo, orgao_vinculado) VALUES
(5, 'Secretaria de Saude', 'Prefeitura Municipal de Palhoca'),
(6, 'Secretaria de Educacao', 'Prefeitura Municipal de Palhoca');

-- ---------------------------------------------------------------------
-- BAIRROS DE PALHOCA
-- ---------------------------------------------------------------------
INSERT INTO bairro (nome, populacao_estimada, area_km2) VALUES
    ('Ponte do Imaruim', 29450, 6.20),
    ('Passa Vinte', 21245, 5.80),
    ('Bela Vista', 20870, 4.10),
    ('Centro', 20180, 3.60),
    ('Aririú', 17930, 9.40),
    ('São Sebastião', 16940, 7.80),
    ('Barra do Aririú', 16740, 8.60),
    ('Caminho Novo', 14525, 6.70),
    ('Brejaru', 13330, 10.20),
    ('Rio Grande', 12795, 8.90),
    ('Pedra Branca', 9910, 12.40),
    ('Pachecos', 9055, 7.10),
    ('Guarda do Cubatão', 7470, 58.00),
    ('Alto Aririú', 7110, 14.50),
    ('Pinheira', 5245, 42.00),
    ('Enseada de Brito (Ens Brito)', 4417, 61.00),
    ('Praia de Fora', 4199, 28.00),
    ('Praia de Fora - Loteamento Marivone', 4083, 6.00),
    ('Jardim Eldorado', 3772, 2.80),
    ('Aririú da Formiga', 3682, 11.30),
    ('Passagem do Massiambu', 2601, 88.00),
    ('Furadinho', 2412, 12.70),
    ('Ponta do Papagaio', 1586, 22.00),
    ('Guarda do Embaú (Ens Brito)', 1007, 35.00),
    ('Morretes II', 998, 25.00),
    ('Massiambu Pequeno', 632, 30.00),
    ('Três Barras', 523, 34.00),
    ('Albardão', 490, 29.00),
    ('Sertão do Campo', 316, 40.00),
    ('Cidade Universitária Pedra Branca', 500, 18.00),
    ('Enseada de Brito', 800, 45.00),
    ('Frei Damião', 1200, 15.00),
    ('Guarda do Embaú', 900, 33.00),
    ('Massiambu', 1500, 95.00),
    ('Pacheco', 1100, 6.50),
    ('Ponte do Imaruim - Centro', 2500, 2.40),
    ('Pagani', 14500, 5.20),
    ('Vila Nova', 6200, 4.80),
    ('Nova Palhoça', 8300, 9.10),
    ('Madri', 5400, 5.50);

-- ---------------------------------------------------------------------
-- PONTOS DE INTERESSE REAIS
-- ---------------------------------------------------------------------
INSERT INTO ponto_de_interesse (nome, latitude, longitude, endereco, id_bairro, categoria) VALUES
    ('UBS Central', -27.6470135, -48.6676051, 'Rua Amaro F. de Macedo, s/n - Centro, CEP 88130-280', 4, 'SAUDE'),
    ('UBS Passa Vinte', -27.6346568, -48.6713562, 'Rua Teodora Hening, 48 - Passa Vinte, CEP 88134-155', 2, 'SAUDE'),
    ('UBS Bela Vista', -27.6579923, -48.6930084, 'Rua Jose Cosme Pamplona, 1447 - Bela Vista, CEP 88130-000', 3, 'SAUDE'),
    ('UBS Ponte do Imaruim', -27.6330999, -48.6471663, 'Rua Trinta e Um de Marco, s/n - Ponte do Imaruim, CEP 88130-350', 1, 'SAUDE'),
    ('UBS Rio Grande', -27.6616351, -48.6640066, 'Avenida Rio Grande, 1744 - Rio Grande, CEP 88131-601', 10, 'SAUDE'),
    ('UBS Barra do Aririu', -27.6795833, -48.6423823, 'Rua Genésio Francisco Martins, s/n - Barra do Aririu, CEP 88134-600', 7, 'SAUDE'),
    ('UBS Enseada de Brito', -27.7687446, -48.6312601, 'Rua Nossa Senhora do Rosario, s/n - Enseada de Brito, CEP 88138-800', 31, 'SAUDE'),
    ('UBS Pinheira', -27.8915168, -48.5929074, 'Rua Aderbal Ramos da Silva, s/n - Pinheira, CEP 88138-853', 15, 'SAUDE'),
    ('UBS Guarda do Cubatao', -27.6944023, -48.6840544, 'Rua Santo Anjo da Guarda, 1832 - Guarda do Cubatao, CEP 88135-330', 13, 'SAUDE'),
    ('UBS Medio Aririu', -27.6676377, -48.6760361, 'Avenida Bom Jesus de Nazare, s/n - Aririu, CEP 88135-000', 5, 'SAUDE'),
    ('UBS Pachecos', -27.6851761, -48.6680418, 'Rua Januario Pereira de Lima, 45 - Pachecos, CEP 88135-380', 12, 'SAUDE'),
    ('UBS Cambirela', -27.7107863, -48.6489322, 'Rua Domingos de Souza Filho, 1836 - Furadinho, CEP 88138-100', 22, 'SAUDE'),
    ('UBS Frei Damiao', -27.6134244, -48.6686399, 'Rua das Palmeiras, s/n - Frei Damiao', 32, 'SAUDE'),
    ('UBS Aririu da Formiga', -27.6916274, -48.6546612, 'Rua Raul Antonio da Silva, s/n - Aririu da Formiga, CEP 88134-770', 20, 'SAUDE'),
    ('UBS Sao Sebastiao', -27.6423090, -48.7167466, 'Rua dos Acores - Sao Sebastiao, CEP 88136-520', 6, 'SAUDE'),
    ('UBS Jardim Eldorado', -27.6242193, -48.6623716, 'Rua Vitor Meirelles, s/n - Jardim Eldorado, CEP 88133-370', 19, 'SAUDE'),
    ('UPA Centro 24h', -27.6597568, -48.6847046, 'Rua Sao Matheus, s/n - Bela Vista, CEP 88132-715', 3, 'SAUDE'),
    ('Hospital Municipal de Palhoca', -27.6645669, -48.6637556, 'Avenida Vidal Procopio Lohn, s/n - Nova Palhoca', 39, 'SAUDE'),
    ('Faculdade Municipal de Palhoca - FMP', -27.6362062, -48.6525856, 'Rua Joao Pereira dos Santos, 99 - Ponte do Imaruim, CEP 88130-475', 1, 'EDUCACAO'),
    ('EEB Irma Maria Teresa', -27.6343910, -48.6459778, 'Avenida Aniceto Zacchi, 298 - Ponte do Imaruim, CEP 88130-300', 1, 'EDUCACAO'),
    ('EEB Gov. Pedro Ivo Figueiredo de Campos', -27.6281014, -48.6527451, 'Rua Nascente do Sol - Ponte do Imaruim, CEP 88130-570', 1, 'EDUCACAO'),
    ('EMEF Prof. Venceslau Bueno', -27.6460477, -48.6695074, 'Rua Jose Alfredo de Brum, 97 - Centro, CEP 88130-280', 4, 'EDUCACAO');

-- Subtipos de saude (id = id_ponto)
INSERT INTO unidade_saude (id_ponto, tipo, especialidades, horario_funcionamento, possui_atendimento_24h) VALUES
    (1, 'UBS', 'CLINICO GERAL', 'Segunda a sexta, 08h as 17h', FALSE),
    (2, 'UBS', 'CLINICO GERAL', 'Segunda a sexta, 08h as 17h', FALSE),
    (3, 'UBS', 'CLINICO GERAL', 'Segunda a sexta, 08h as 17h', FALSE),
    (4, 'UBS', 'CLINICO GERAL', 'Segunda a sexta, 08h as 17h', FALSE),
    (5, 'UBS', 'CLINICO GERAL', 'Segunda a sexta, 08h as 17h', FALSE),
    (6, 'UBS', 'CLINICO GERAL', 'Segunda a sexta, 08h as 17h', FALSE),
    (7, 'UBS', 'CLINICO GERAL', 'Segunda a sexta, 08h as 17h', FALSE),
    (8, 'UBS', 'CLINICO GERAL', 'Segunda a sexta, 08h as 17h', FALSE),
    (9, 'UBS', 'CLINICO GERAL', 'Segunda a sexta, 08h as 17h', FALSE),
    (10, 'UBS', 'CLINICO GERAL', 'Segunda a sexta, 08h as 17h', FALSE),
    (11, 'UBS', 'CLINICO GERAL', 'Segunda a sexta, 08h as 17h', FALSE),
    (12, 'UBS', 'CLINICO GERAL', 'Segunda a sexta, 08h as 17h', FALSE),
    (13, 'UBS', 'CLINICO GERAL', 'Segunda a sexta, 08h as 17h', FALSE),
    (14, 'UBS', 'CLINICO GERAL', 'Segunda a sexta, 08h as 17h', FALSE),
    (15, 'UBS', 'CLINICO GERAL', 'Segunda a sexta, 08h as 17h', FALSE),
    (16, 'UBS', 'CLINICO GERAL', 'Segunda a sexta, 08h as 17h', FALSE),
    (17, 'UPA', 'EMERGENCIA', 'Todos os dias, 24 horas', TRUE),
    (18, 'Hospital', 'CIRURGIA / INTERNAMENTO', 'Todos os dias, 24 horas', TRUE);

-- Subtipos de educacao (id = id_ponto)
INSERT INTO instituicao_ensino (id_ponto, tipo, esfera, niveis_atendidos, possui_vagas_abertas) VALUES
    (19, 'Universidade', 'Publica', 'Graduacao e pos-graduacao', TRUE),
    (20, 'Escola Estadual', 'Publica', 'Ensino Fundamental e Medio', TRUE),
    (21, 'Escola Estadual', 'Publica', 'Ensino Fundamental e Medio', FALSE),
    (22, 'Escola Municipal', 'Publica', 'Ensino Fundamental', FALSE);

-- ---------------------------------------------------------------------
-- OCORRENCIAS DE SEGURANCA (ficticias, sem identificacao de vitimas - RN02)
-- ---------------------------------------------------------------------
INSERT INTO ocorrencia_seguranca (id_usuario_cidadao, id_bairro, tipo_evento, data_hora, nivel_gravidade, status_aprovacao) VALUES
    (2, 4, 'Iluminacao publica deficiente', TIMESTAMP '2026-02-10 20:30:00', 'MEDIA', TRUE),
    (3, 3, 'Furto de veiculo', TIMESTAMP '2026-02-12 19:15:00', 'ALTA', FALSE),
    (4, 1, 'Vandalismo', TIMESTAMP '2026-02-15 22:00:00', 'BAIXA', TRUE),
    (2, 2, 'Roubo a transeunte', TIMESTAMP '2026-03-02 21:10:00', 'ALTA', TRUE),
    (3, 37, 'Suspeita de furto', TIMESTAMP '2026-03-08 18:45:00', 'MEDIA', FALSE),
    (4, 10, 'Depredacao de patrimonio', TIMESTAMP '2026-03-11 23:20:00', 'BAIXA', TRUE);

INSERT INTO avaliacao_cobertura_saude (id_gestor, id_bairro, total_leitos_upa, total_equipes_ubs, indice_atendimento_habitante) VALUES
    (5, 3, 16, 4, 0.85),
    (5, 4, 0, 3, 0.72),
    (5, 1, 0, 2, 0.61);

INSERT INTO planejamento_educacional (id_gestor, id_bairro, regiao_mapeada, demanda_estimada_alunos, total_escolas_disponiveis) VALUES
    (6, 4, 'Centro', 2500, 2),
    (6, 1, 'Ponte do Imaruim', 3200, 3),
    (6, 5, 'Aririu', 1800, 1);

-- ---------------------------------------------------------------------
-- CONSULTAS DE CONFERENCIA (JOINs)
---------------------------------------------------------------------
SELECT p.nome AS ponto, p.categoria, p.endereco, b.nome AS bairro,
       p.latitude, p.longitude
FROM ponto_de_interesse p
JOIN bairro b ON p.id_bairro = b.id_bairro
ORDER BY p.categoria, b.nome, p.nome;

SELECT p.nome AS unidade, u.tipo, u.especialidades, u.horario_funcionamento, b.nome AS bairro
FROM unidade_saude u
JOIN ponto_de_interesse p ON u.id_ponto = p.id_ponto
JOIN bairro b ON p.id_bairro = b.id_bairro;

SELECT b.nome AS bairro, o.tipo_evento, o.nivel_gravidade, o.status_aprovacao
FROM ocorrencia_seguranca o
JOIN bairro b ON o.id_bairro = b.id_bairro
ORDER BY b.nome;
