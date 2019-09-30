/*
*/
function main(inputObj) {
    for (key in helloWorlder) { print(key); }
    for (i in inputObj) {
        inputObj[i] = helloWorlder.helloWorld(inputObj[i]);
        print(i + "value: " + inputObj[i]);
    }

    return inputObj;
}