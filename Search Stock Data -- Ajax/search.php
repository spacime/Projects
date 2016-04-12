<?php
header("Access-Control-Allow-Origin: *");

if (isset($_GET["searchMore"])) {
    $more_information_URL = "http://dev.markitondemand.com/MODApis/Api/v2/Quote/json?symbol=" . $_GET["searchMore"];
    $json_content = file_get_contents($more_information_URL);
    echo $json_content;
}

if (isset($_GET["search_query"])) {
    $url = "http://dev.markitondemand.com/MODApis/Api/v2/Lookup/json?input=" . $_GET["search_query"];
    $json = file_get_contents($url);
    $json_decode = json_decode($json);

    foreach ($json_decode as $lookup) {
        $arr[$lookup->{'Symbol'}] = $lookup;
    }

    $json = json_encode($arr);
    echo "{$_GET['json1callback']}({$json})";
}


if(isset($_GET["input"])) {
    $autocomplete_check_search = "http://dev.markitondemand.com/MODApis/Api/v2/Lookup/json?input=".$_GET["input"];
    $json_content = file_get_contents($autocomplete_check_search);
    echo $json_content;
}

if (isset($_GET['bingVal'])) {
    $accountKey = 'Qmfhi26fiV82vm+tid3PqGtOgKwGdRUpINX4ENVA7o4';
    $ServiceRootURL = 'https://api.datamarket.azure.com/Bing/Search/v1/News?Query=%27';
    $WebSearchURL = $ServiceRootURL;

    $context = stream_context_create(array(
        'http' => array(
            'request_fulluri' => true,
            'header' => "Authorization: Basic " . base64_encode($accountKey . ":" . $accountKey)
        )
    ));

    $request = $WebSearchURL . urlencode('\'' . $_GET['bingVal'] . '\'') . '%27&$format=json';
    $response = file_get_contents($request, 0, $context);

    echo $response;
}
?>