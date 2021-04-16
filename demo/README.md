# Guião de Demonstração

## 1. Preparação do sistema

Para testar o sistema e todos os seus componentes, é necessário preparar um ambiente com dados para proceder à verificação dos testes.

### 1.1. Lançar o *registry*

Para lançar o *ZooKeeper*, ir à pasta `zookeeper/bin` e correr o comando
`./zkServer.sh start` (Linux) ou `zkServer.cmd` (Windows).

É possível também lançar a consola de interação com o *ZooKeeper*, novamente na pasta `zookeeper/bin` e correr `./zkCli.sh` (Linux) ou `zkCli.cmd` (Windows).

### 1.2. Compilar o projeto

Primeiramente, é necessário compilar e instalar todos os módulos e suas dependências -- *rec*, *hub*, *app*, etc. Para isso, basta ir à pasta *root* do projeto e correr o seguinte comando:

```
$ mvn clean install -DskipTests
```

### 1.3. Lançar e testar o *rec*

Para proceder aos testes, é preciso em primeiro lugar lançar o servidor *rec*  e o servidor *hub*. Para isso basta ir à pasta *rec* e *hub* e executar:

```
$ mvn compile exec:java
```

Este comando vai colocar o *rec* no endereço *localhost* e na porta *8091* e o *hub* no endereço *localhost* e na porta *8081*.

Para confirmar o funcionamento do servidor com um *ping*, fazer:

```
$ cd rec-tester
$ mvn compile exec:java
```

Para executar toda a bateria de testes de integração, fazer:

```
$ mvn verify
```

Todos os testes devem ser executados sem erros.

### 1.4. Lançar e testar o *hub*

Para proceder aos testes, é preciso lançar o servidor *rec* e *hub* . Para isso basta ir à pasta *rec* e *hub* e executar:

```
$ mvn compile exec:java
```

Este comando vai colocar o *rec* no endereço *localhost* e na porta *8091* e o *hub* no endereço *localhost* e na porta *8081*.

Para confirmar o funcionamento do servidor com um *ping*, fazer:

```
$ cd hub-tester
$ mvn compile exec:java
```

Para executar toda a bateria de testes de integração, fazer:

```
$ mvn verify
```

Todos os testes devem ser executados sem erros.

### 1.5. *App*

Desligar os servidores *hub* e *rec* e iniciar novamente. Para isso, ir à pasta *rec* e depois *hub* e executar:

```
$ mvn compile exec:java
```

Iniciar a aplicação com a utilizadora alice:

```
$ app localhost 2181 alice +35191102030 38.7380 -9.3000
```

**Nota:** Para poder correr o script *app* diretamente é necessário fazer `mvn install` e adicionar ao *PATH* ou utilizar diretamente os executáveis gerados na pasta `target/appassembler/bin/`.

Abrir outra consola, e iniciar a aplicação com o utilizador bruno.

```
$ app localhost 2181 bruno +35193334444 38.6867 -9.3124
```

Depois de lançar todos os componentes, tal como descrito acima, já temos o que é necessário para usar o sistema através dos comandos.

## 2. Teste dos comandos

Nesta secção vamos correr os comandos necessários para testar todas as operações do sistema. Cada subsecção é respetiva a cada operação presente no *hub*.

### 2.1. *balance*

Caso de sucesso para alice:

```
> balance
alice 0 BIC
```

Caso de sucesso para bruno:

```
> balance
bruno 0 BIC
```

### 2.2. *top-up*

Caso de sucesso para alice:

```
> top-up 15
alice 150 BIC
```

Caso de sucesso para bruno:

```
> top-up 20
bruno 200 BIC
```

Caso de insucesso para ambos:

```
> top-up 30
ERRO: INVALID_ARGUMENT: Só se pode carregar com valores entre 1 EUR e 20 EUR, inclusive.
```

### 2.3 *info-station*

Caso de sucesso para ambos:

```
> info istt
IST Taguspark, lat 38.7372, -9.3023 long, 20 docas, 4 BIC prémio, 12 bicicletas, 8 levantamentos, 0 devoluções, https://www.google.com/maps/place/38.7372,-9.3023
```

Caso de insucesso para ambos:
```
> info amadora
ERRO: INVALID ARGUMENT: Não existe nenhuma estação com a abreviatura: amadora
```
### 2.4 *locate_station*

Caso de sucesso para alice:

```
> scan 3
istt lat 38.7372, -9.3023 long, 20 docas, 4 BIC prémio, 12 bicicletas, a 218 metros
stao lat 38.6867, -9.3124 long, 30 docas, 3 BIC prémio, 20 bicicletas, a 5805 metros
jero lat 38.6972, -9.2064 long, 30 docas, 3 BIC prémio, 20 bicicletas, a 9302 metros
```

Caso de sucesso para bruno:

```
> scan 3
stao lat 38.6867, -9.3124 long, 30 docas, 3 BIC prémio, 20 bicicletas, a 0 metros
istt lat 38.7372, -9.3023 long, 20 docas, 4 BIC prémio, 12 bicicletas, a 5683 metros
jero lat 38.6972, -9.2064 long, 30 docas, 3 BIC prémio, 20 bicicletas, a 9273 metros
```

### 2.5 *bike-up*

Caso de sucesso para bruno:

```
> bike-up stao
OK
```

Caso de insucesso para alice:

```
> bike-up istt
ERRO: INVALID_ARGUMENT: Fora de alcance
```

### 2.6 *bike-down*

Caso de sucesso para bruno:

```
> bike-down stao
OK
```

Caso de insucesso para alice:

```
> bike-down stao
ERRO: INVALID_ARGUMENT: Fora de alcance
```

Caso de insucesso para ambos:

```
> bike-down gulb
ERRO: INVALID_ARGUMENT: Fora de alcance
```

### 2.7 *ping*

Caso de sucesso para ambos:

```
> ping friend
friend
```

Caso de insucesso para ambos:

```
> ping
ERRO: INVALID_ARGUMENT: Error ping: null or empty
```

### 2.8 sys_status

Caso de sucesso para ambos:

```
> sys_status status
/grpc/bicloin/hub/1 up
/grpc/bicloin/rec/1 up
```

---

Nesta secção vamos correr os comandos necessários para testar todas as operações do sistema. Cada subsecção é respetiva a cada operação presente na *app*.

### 2.9 *tag*

Caso de sucesso para ambos:

```
> tag 38.7376 -9.3031 loc1
OK
```

Caso de insucesso para ambos:

```
> tag istt ista loc1
ERRO: Impossivel criar uma tag com os valores:istt e ista
```

### 2.10 *move*

Caso de sucesso para alice (assumindo que loc1 é uma tag):

```
> move loc1 
alice em https://www.google.com/maps/place/38.7376,-9.3031
```

Caso de sucesso para bruno (assumindo que loc1 é uma tag):

```
> move loc1 
bruno em https://www.google.com/maps/place/38.7376,-9.3031
```

Caso de sucesso para alice:

```
> move 38.6867 -9.3117
alice em https://www.google.com/maps/place/38.6867,-9.3117
```

Caso de sucesso para bruno:

```
> move 38.6867 -9.3117
bruno em https://www.google.com/maps/place/38.6867,-9.3117
```

Caso de insucesso para ambos (istt não é uma tag):

```
> move istt
ERRO: Não existe nenhuma tag com o nome: istt
```

### 2.11 *at*

Caso de sucesso para alice:

```
> at
alice em https://www.google.com/maps/place/38.6867,-9.3117
```

Caso de sucesso para bruno:

```
> at
bruno em https://www.google.com/maps/place/38.6867,-9.3117
```

### 2.12 *exit*

Caso de sucesso para ambos:

```
> exit
Até à próxima!!!
```

------



## 3. Considerações Finais

Quando é inserido um utilizador ou uma estação através dos ficheiros csv que não verifica as condições, é ignorado a passa
a ler o seguinte elemento.

Para correr com os ficheiros com os comandos (*comandosAlice.txt* e *comandosBruno.txt*) é necessário lançar os servidores e
na pasta app fazer:\
Para a alice:
```
mvn exec:java -Dexec.args="localhost 2181 alice +35191102030 38.7380 -9.3000" < comandosAlice.txt
```
Para o bruno:
```
mvn exec:java -Dexec.args="localhost 2181 bruno +35193334444 38.6867 -9.3124" < comandosBruno.txt
```

Estes testes não cobrem tudo, pelo que devem ter sempre em conta os testes de integração e o código.