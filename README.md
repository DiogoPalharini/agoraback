# Backend - Guia de Configuração e Execução

Este guia descreve como configurar e executar o backend do projeto.

---

## **Pré-requisitos**

Certifique-se de ter instalado em sua máquina:
- **Node.js**
- **JDK 23**
- Um IDE, como **IntelliJ IDEA** (ou outra de sua preferência).
- **MySQL** ou outro banco de dados compatível.

---

## **Passo a Passo**

### **1. Clonar o Repositório**
Clone o repositório para sua máquina local usando o comando abaixo:
```bash
git clone https://github.com/A-Sync-Fatec/api-back-2-24.git
```


### **2. Configurar o Banco de Dados**
Certifique-se de que o MySQL está instalado e rodando.
Acesse o MySQL Workbench (ou outro cliente de banco de dados) e crie um banco de dados com o nome api. Execute o comando:
```bash
CREATE DATABASE api;
```
Localize o arquivo de configuração no diretório:
```bash
src/main/resources/application.properties
```
Abra o arquivo e atualize os seguintes campos com suas credenciais do MySQL:
```bash
spring.datasource.url=jdbc:mysql://localhost:3306/api
spring.datasource.username=seu_usuario
spring.datasource.password=sua_senha
```
### **3. Instalar Dependências**
No diretório raiz do projeto backend, instale as dependências necessárias usando o comando:3. Instalar Dependências
No diretório raiz do projeto backend, instale as dependências necessárias usando o comando:
```bash
npm install
```
### **4. Executar o Backend**
Abra sua IDE de preferência (como IntelliJ IDEA).
Navegue até o arquivo principal da aplicação:
```bash
src/main/java/com/example/api2024/Application.java
```
Abra este arquivo e execute a aplicação. Normalmente, isso pode ser feito clicando no botão de Run ou com o atalho de execução da sua IDE.
