
const BOARD_SIZE = 8;
const CANVAS_SIZE = 800;
const LINE_WIDTH = 8;

const WHITE = "W";
const BLACK = "B";

let board = Array.from(Array(BOARD_SIZE), () => new Array(BOARD_SIZE));
let result = null;
let nextPlayer = null;

function setup() {
    let canvas = createCanvas(CANVAS_SIZE, CANVAS_SIZE);
    canvas.parent("board");
    background(29, 150, 37);

    requestNewGame();
}

function draw() {
    drawBoard();
}

function drawBoard() {
    background(29, 150, 37);
    let tileWidth = CANVAS_SIZE / BOARD_SIZE;

    for(let i=0; i<BOARD_SIZE; i++) {
        strokeWeight(LINE_WIDTH);
        stroke(0);
        line(i*tileWidth, 0, i*tileWidth, CANVAS_SIZE);
        line(0, i*tileWidth, CANVAS_SIZE, i*tileWidth);
    }

    strokeWeight(0);
    for(let x=0; x<board.length; x++) {
        for(let y=0; y<board.length; y++) {
            if (board[x][y] == "W") {
                fill(256);
            } else if (board[x][y] == "B") {
                fill(0);
            } else {
                continue;
            }
            circle(x*tileWidth + tileWidth/2, CANVAS_SIZE - (y*tileWidth + tileWidth/2), tileWidth - LINE_WIDTH);
        }
    }
}

function mouseClicked() {
    let px = mouseX;
    let py = CANVAS_SIZE - mouseY;
    let tileWidth = CANVAS_SIZE / BOARD_SIZE;

    let x = Math.floor(px / tileWidth);
    let y = Math.floor(py / tileWidth);
    console.log(x, y);
    requestMove(x, y);
}

function requestNewGame() {
    let req = new XMLHttpRequest();
    req.addEventListener("load", () => {
        onMoveResponse(req);
    });
    req.open("POST", "http://localhost:8001/new");
    req.send();
}

function requestMove(x, y) {
    let req = new XMLHttpRequest();
    req.addEventListener("load", () => {
        onMoveResponse(req);
    });
    req.open("POST", "http://localhost:8001/move");
    req.send("("+x+","+y+")");
}

function onMoveResponse(req) {
    console.log(req.response);
    response = JSON.parse(req.response);
    if (response.moves) {
        states = response.moves;
        setMove(states[0]);
        setTimeout(() => {
            setMove(states[1])
        }, 2000);
    } else {
        setMove(response);
    }
}

function setMove(move) {
    board = move.state.board;
    if (move.result) {
        result = move.result;
        diff = move.diff;
        setDirectionsForResult(result, diff);
    } else {
        nextPlayer = move.nextPlayer;
        setDirectionsForTurn(nextPlayer);
    }
}

function setDirectionsForResult(winnerSymbol, differential) {
    let winnerTotal = 0;
    let loserTotal = 0;
    for (let x=0; x<BOARD_SIZE; x++) {
        for (let y=0; y<BOARD_SIZE; y++) {
            if (board[x][y] === winnerSymbol) {
                winnerTotal++;
            } else if (board[x][y] !== '_') {
                loserTotal++;
            }
        }
    }
    let winner = winnerSymbol.trim() === 'W' ? "White" : "Black";
    let directions = "Winner: " + winner + "\n" + winnerTotal + "-" + loserTotal;
    setDirections(directions);
}

function setDirectionsForTurn(nextPlayerSymbol) {
    let nextPlayer = nextPlayerSymbol.trim() === 'W' ? "White" : "Black";
    let directions = nextPlayer + " goes next.";
    setDirections(directions);
}

function setDirections(directions) {
    document.getElementById("directions").innerHTML = directions;
}
