/*
This file will be prepended to any script provided to the sandbox.  It provides a function that will take a
JSON string, convert it to a JS object and invoke the user-provided main() function.

It will the convert the returning object back to JSON for return to the Sandbox.
*/

function _main(inputObjs) {
    return main(inputObjs);
    // TODO: Should have some check here that main() has been declared?
//    var parsedObj = JSON.parse(inputObjs);
//    ret = main(parsedObj);
//    return JSON.stringify(ret);
}