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

### 1.3. Lançar e testar 2 *recs*

Para proceder aos testes, é preciso em primeiro lugar lançar os servidores *rec*  e o servidor *hub*. Para isso basta ir à pasta *rec* e executar:

```
$ mvn exec:java -Dexec.args="localhost 2181 localhost 8091 1"
```
Este comando vai colocar o *rec* no endereço *localhost* e na porta *8091*.

Num novo terminal, ir à pasta *rec* e executar o comando:
```
$ mvn exec:java -Dexec.args="localhost 2181 localhost 8092 2"
```

Este comando vai colocar o *rec* no endereço *localhost* e na porta *8092*.

Posteriormente, num novo terminal ir à pasta *hub* e executar:
```
$ mvn exec:java
```

Este comando vai colocar o *hub* no endereço *localhost* e na porta *8081*.

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

Para proceder aos testes, é preciso em primeiro lugar lançar os servidores *rec*  e o servidor *hub*. Para isso basta ir à pasta *rec* e executar:

```
$ mvn exec:java -Dexec.args="localhost 2181 localhost 8091 1"
```
Este comando vai colocar o *rec* no endereço *localhost* e na porta *8091*.

Num novo terminal, ir à pasta *rec* e executar o comando:
```
$ mvn exec:java -Dexec.args="localhost 2181 localhost 8092 2"
```

Este comando vai colocar o *rec* no endereço *localhost* e na porta *8092*.

Posteriormente, num novo terminal ir à pasta *hub* e executar:
```
$ mvn exec:java
```
Este comando vai colocar o *hub* no endereço *localhost* e na porta *8081*.

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

Desligar os servidores *hub* e *rec* e iniciar novamente. Para isso, ir à pasta *rec* e executar o comando:

```
$ mvn exec:java -Dexec.args="localhost 2181 localhost 8091 1"
```

Num novo terminal, ir à pasta *rec* e executar o comando:
```
$ mvn exec:java -Dexec.args="localhost 2181 localhost 8092 2"
```

Se desejar iniciar mais Recs, é só ir à pasta *rec* e executar o comando:
```
$ mvn exec:java -Dexec.args="localhost 2181 localhost 809i i"
```
onde i = número da instância. i deve ser sempre incrementado com 1 unidade relativamente ao anterior.

Posteriormente, abrir um novo terminal, ir à pasta *hub* e executar o comandp:
```
$ mvn exec:java
```

Iniciar a aplicação com a utilizadora alice executando o seguinte comando:

```
$ mvn exec:java -Dexec.args="localhost 2181 alice +35191102030 38.7380 -9.3000"
```

**Nota:** Para poder correr o script *app* diretamente é necessário fazer `mvn install` e adicionar ao *PATH* ou utilizar diretamente os executáveis gerados na pasta `target/appassembler/bin/`.

Abrir outra consola, e iniciar a aplicação com o utilizador bruno executando o seguinte comando:

```
$ mvn exec:java -Dexec.args="localhost 2181 bruno +35193334444 38.6867 -9.3124"
```

Depois de lançar todos os componentes, tal como descrito acima, já temos o que é necessário para usar o sistema através dos comandos.

## 2. Teste dos comandos

Nesta secção vamos correr os comandos necessários para testar todas as operações do sistema. Cada subsecção é respetiva a cada operação presente no *hub*.

Ao longo da execução dos comandos, o utilizador é informado da réplica a que se conectou através da seguinte mensagem:

```
Conectei-me à réplica i no localhost 809i
```
onde i é o número da instância da réplica.
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
ERRO: INVALID_ARGUMENT: Não existe nenhuma estação com essa abreviatura.
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
ERRO: INVALID_ARGUMENT: Fora de alcance.
```

Suspender o Rec com a maior instância executando ```CTRL+Z``` no teclado, no terminal correspondente.

### 2.6 *top-up*

Caso de sucesso para alice:
```
> top-up 10
alice 250 BIC
```

Caso de sucesso para bruno:
```
> top-up 10
bruno 290 BIC
```


### 2.7 *bike-down*

Caso de sucesso para bruno:

```
> bike-down stao
OK
```

Caso de insucesso para alice:

```
> bike-down stao
ERRO: INVALID_ARGUMENT: Fora de alcance.
```

Caso de insucesso para ambos:

```
> bike-down gulb
ERRO: INVALID_ARGUMENT: Fora de alcance.
```

Escrever no terminal do Rec suspenso ```fg```, no terminal correspondente.

### 2.8 *top-up*

Caso de sucesso para alice:
```
> top-up 5
alice 300 BIC
```

Caso de sucesso para bruno:
```
> top-up 5
bruno 343 BIC
```

###2.9 *balance*
Caso de sucesso para alice:
```
> balance
alice 300 BIC
```

Caso de sucesso para bruno:
```
> balance
bruno 343 BIC
```

É de notar, que aparece na réplica que esteve suspensa, o valor de dinheiro atualizado, contanto com o top-up feito quando a réplica estava adormecida.

### 2.10 *ping*

Caso de sucesso para ambos:

```
> ping
PONG
```

### 2.11 sys_status

Caso de sucesso para ambos:

```
> sys_status status
/grpc/bicloin/hub/1 up
/grpc/bicloin/rec/1 up
/grpc/bicloin/rec/2 up
...
```
Se existirem mais recs, aparece no terminal /grpc/bicloin/rec/i up/down, onde i é o número da instância e up ou down consoante se os servidores estão ligados ou não. 

---

Nesta secção vamos correr os comandos necessários para testar todas as operações do sistema. Cada subsecção é respetiva a cada operação presente na *app*.

### 2.12 *tag*

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

### 2.13 *move*

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

### 2.14 *at*

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

### 2.15 *zzz*
Caso de sucesso para ambos:
```
> zzz 2000
Dormi durante 2000 milissegundos!
```

### 2.16 *exit*

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