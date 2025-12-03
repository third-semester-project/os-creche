CREATE DATABASE os_creche

CREATE TYPE perfil_enum AS ENUM ('ADMIN', 'GESTOR', 'OPERADOR');

CREATE TABLE usuarios (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    perfil perfil_enum NOT NULL,
    senha_hash TEXT NOT NULL,
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    criado_em   TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    atualizado_em TIMESTAMPTZ
);

CREATE TABLE ordens_servico (
    id BIGSERIAL PRIMARY KEY,
    numero VARCHAR(100) NOT NULL UNIQUE,
    titulo VARCHAR(255) NOT NULL,
    descricao TEXT,
    categoria VARCHAR(100),
    prioridade VARCHAR(100),
    status VARCHAR(100),
    solicitante VARCHAR(255),
    data_abertura TIMESTAMPTZ,
    prazo TIMESTAMPTZ,
    data_conclusao TIMESTAMPTZ,
    criado_por BIGINT,
    atribuido_para BIGINT,
    observacoes TEXT,
    criado_em   TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    atualizado_em TIMESTAMPTZ,
    CONSTRAINT fk_criado_por
    FOREIGN KEY (criado_por) REFERENCES usuarios(id),
    CONSTRAINT fk_atribuido_para
    FOREIGN KEY (atribuido_para) REFERENCES usuarios(id)
);

INSERT INTO usuarios (nome, email, perfil, senha_hash, ativo)
VALUES ('Murilo', 'admin@creche.com', 'ADMIN', '$2a$10$u/tp.C.nVUp.EIf4poStwOh9IPEeoAvyvjBExHRmXwBGxX7yuqzIe', TRUE);