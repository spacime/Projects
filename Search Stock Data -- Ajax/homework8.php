<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <link rel="stylesheet" href="css/bootstrap.min.css">
    <link href="https://gitcdn.github.io/bootstrap-toggle/2.2.2/css/bootstrap-toggle.min.css" rel="stylesheet">
    <link rel="stylesheet" href="//code.jquery.com/ui/1.11.4/themes/smoothness/jquery-ui.css">
    <title>Stock Market Search</title>
    <style>
        .icon-c {
            color: yellow;
        }
    </style>
</head>
<body style="background-color: rgb(0, 95, 117)">
<br>
<!--Search pannel-->
<div class="container">
    <div class="panel panel-default">
        <div class="container">
            <h2 align="center">Stock Market Search</h2>
        </div>
        <div class="container">
            <form class="form-group" action="<?php echo htmlspecialchars($_SERVER["PHP_SELF"]); ?>" method="post"
                  name="search_form">

                <div class="row">
                    <div class="col-md-4">
                        <label>Enter the stock name or symbol:<span style="color: red" class="redstar">*</span></label>
                    </div>
                    <div class="col-md-4">
                        <input id="search_query" name="search_query" class="form-control"
                               placeholder="Apple Inc or AAPL" type="text" autocomplete="off"
                               value=<?php
                        if ($_SERVER["REQUEST_METHOD"] == "POST") {
                            echo $_POST["search_query"];
                        }
                        ?>>
                    </div>
                    <div class="col-md-4">
                        <button type="submit" class="btn btn-primary" aria-label="Left Align">
                            <span class="glyphicon glyphicon-search" aria-hidden="true"></span> Get Quote
                        </button>
                        <button type="button" class="btn btn-default" aria-label="Left Align">
                            <span class="glyphicon glyphicon-refresh" aria-hidden="true"></span> Clear
                        </button>
                    </div>
                </div>
            </form>
        </div>
    </div>
</div>

<!--HR-->
<div class="container">
    <hr>
</div>
<!--Information pannel-->

<div class="container">
    <div id="myCarousel" class="carousel slide" data-ride="carousel" data-interval="false">
        <!-- Wrapper for slides -->
        <div class="carousel-inner" role="listbox">
            <div class="well item">
                <div class="panel panel-default">
                    <div class="panel-heading clearfix">
                        <h4 class="panel-title pull-left" style="padding-top: 7.5px;">Favourite List</h4>

                        <div class="pull-right">
                            <button type="button" class="btn btn-default" aria-label="Left Align" data-slide="next"
                                    href="#myCarousel" id="next" disabled>
                                <span class="glyphicon glyphicon-chevron-right" aria-hidden="true"></span>
                            </button>
                            &nbsp;
                        </div>

                        <div class="pull-right">
                            <button type="button" class="btn btn-default" aria-label="Left Align">
                                <span class="glyphicon glyphicon-refresh" aria-hidden="true"></span>
                            </button>
                            &nbsp;
                        </div>

                        <div class="pull-right">
                            <input type="checkbox" checked data-toggle="toggle">&nbsp;
                        </div>

                        <div class="pull-right">
                            <h4>Automatic Refresh:&nbsp;</h4>
                        </div>
                    </div>

                    <div class="panel-body">
                        <table id="favoriteList" class="table table-striped">
                            <tr>
                                <th>Symbol</th>
                                <th>Company Name</th>
                                <th>Stock Price</th>
                                <th>Change</th>
                                <th>Market Cap</th>
                            </tr>
                        </table>
                    </div>
                </div>
            </div>

            <div class="well item active">
                <div class="panel panel-default">
                    <div class="panel-heading clearfix">
                        <div class="pull-left">
                            <button type="button" class="btn btn-default" aria-label="Left Align" data-slide="prev"
                                    href="#myCarousel" id="prev" disabled>
                                <span class="glyphicon glyphicon-chevron-left" aria-hidden="true"></span>
                            </button>
                            &nbsp;
                        </div>
                        <h4 style="text-align: center">Stock Details</h4>

                    </div>

                    <div class="panel-body">
                        <ul class="nav nav-pills">
                            <li class="active">
                                <a class="navbar-brand" href="#tab_a" data-toggle="pill">
                                    <span class="glyphicon glyphicon-dashboard"></span>Current Stock
                                </a>
                            </li>
                            <li>
                                <a class="navbar-brand" href="#tab_b" data-toggle="pill" id="tabB">
                                    <span class="glyphicon glyphicon-stats"></span>Historical Charts
                                </a>
                            </li>
                            <li>
                                <a class="navbar-brand" href="#tab_c" data-toggle="pill">
                                    <span class="glyphicon glyphicon-link"></span>New Feeds
                                </a>
                            </li>
                        </ul>
                        <hr>

                        <div class="tab-content">
                            <div class="tab-pane fade in active" id="tab_a">
                                <div class="col-md-6">
                                    <?php
                                    $company = "";
                                    if ($_SERVER["REQUEST_METHOD"] == "POST") {
                                        $company = $_POST["search_query"];

                                        $url_moreinfo = "http://dev.markitondemand.com/MODApis/Api/v2/Quote/json?symbol=" . $company;
                                        $content = file_get_contents($url_moreinfo);
                                        $more_info = json_decode($content);
                                        show_more_info($more_info);
                                    }
                                    function show_more_info($more_info)
                                    {
                                        echo "<table class='table table-striped'>";
                                        if ($more_info->{'Status'} == "SUCCESS") {
                                            echo "<tr><td>Name</td><td id='companyName'>" . $more_info->{'Name'} . "</td></tr>";
                                            echo "<tr><td>Symbol</td><td id='symbol'>" . $more_info->{'Symbol'} . "</td></tr>";
                                            echo "<tr><td>Last Price</td><td id='price'>" . $more_info->{'LastPrice'} . "</td></tr>";
                                            echo "<tr><td>Change</td><td id='change'>" . number_format($more_info->{'Change'}, 2, '.', '') . "</td></tr>";
                                            echo "<tr><td>Change Percent</td><td>" . number_format($more_info->{'ChangePercent'}, 2, '.', '') . "%" . get_img($more_info->{'ChangePercent'}) . "</td></tr>";
                                            echo "<tr><td>Timestamp</td><td>" . date("Y-m-d g:i A T", strtotime($more_info->{'Timestamp'})) . "</td></tr>";
                                            echo "<tr><td>MarketCap</td><td id='marketCap'>" . number_format($more_info->{'MarketCap'} / 1000000000, 2, '.', '') . "B" . "</td></tr>";
                                            echo "<tr><td>Volume</td><td>" . number_format($more_info->{'Volume'}) . "</td></tr>";
                                            if ($more_info->{'ChangeYTD'} < 0) {
                                                echo "<tr><td>ChangeYTD</td><td>(" . number_format($more_info->{'ChangeYTD'}, 2, '.', '') . ")" . get_img($more_info->{'ChangeYTD'}) . "</td></tr>";
                                            } else {
                                                echo "<tr><td>ChangeYTD</td><td>" . number_format($more_info->{'ChangeYTD'}, 2, '.', '') . get_img($more_info->{'ChangeYTD'}) . "</td></tr>";
                                            }
                                            echo "<tr><td>ChangePercentYTD</td><td>" . number_format($more_info->{'ChangePercentYTD'}, 2, '.', '') . "%" . get_img($more_info->{'ChangePercentYTD'}) . "</td></tr>";
                                            echo "<tr><td>High</td><td>" . $more_info->{'High'} . "</td></tr>";
                                            echo "<tr><td>Low</td><td>" . $more_info->{'Low'} . "</td></tr>";
                                            echo "<tr><td>Open</td><td>" . $more_info->{'Open'} . "</td></tr>";
                                        } else {
                                            echo "<tr><td><span style='font-size: 23px; margin: 30px'>There is no stock information available</span></td></tr>";
                                        }
                                        echo "</table>";
                                    }

                                    function get_img($value)
                                    {
                                        $arrow_img = "<img width='12px' height='12px' src='";
                                        if ($value > 0) {
                                            $arrow_img .= "http://cs-server.usc.edu:45678/hw/hw6/images/Green_Arrow_Up.png'>";
                                        } else {
                                            $arrow_img .= "http://cs-server.usc.edu:45678/hw/hw6/images/Red_Arrow_Down.png'>";
                                        }
                                        return $arrow_img;
                                    }

                                    ?>
                                </div>

                                <div class="col-md-6">
                                    <div class="row">
                                        <img class="pull-right" onclick="fbShare()"
                                             src="https://cdn3.iconfinder.com/data/icons/social-media-chamfered-corner/154/facebook-48.png">
                                        <button onclick="addFavoriteList()" type="button"
                                                class="btn btn-default pull-right" style="height:48px;width:48px">
                                            <span class="glyphicon glyphicon-star" aria-hidden="true" id="favorite"
                                                  style="color: silver; font-size: 180%"></span>
                                        </button>
                                    </div>
                                    <?php
                                    if ($_SERVER["REQUEST_METHOD"] == "POST") {
                                        echo '<img src="http://chart.finance.yahoo.com/t?s=' . $_POST["search_query"] . '&lang=en-US&width=500&height=375"/>';
                                    }
                                    ?>
                                </div>
                            </div>
                            <div class="tab-pane fade" id="tab_b">
                                <div class="col-md-12">
                                    <div id="chartContainer">
                                    </div>
                                    <div id="chartDemoContainer">
                                    </div>
                                </div>
                            </div>
                            <div class="tab-pane fade" id="tab_c">
                                <?php
                                if ($_SERVER["REQUEST_METHOD"] == "POST") {
                                    $accountKey = 'Qmfhi26fiV82vm+tid3PqGtOgKwGdRUpINX4ENVA7o4';
                                    $ServiceRootURL = 'https://api.datamarket.azure.com/Bing/Search/v1/News?Query=%27';
                                    $WebSearchURL = $ServiceRootURL;

                                    $context = stream_context_create(array(
                                        'http' => array(
                                            'request_fulluri' => true,
                                            'header' => "Authorization: Basic " . base64_encode($accountKey . ":" . $accountKey)
                                        )
                                    ));

                                    $request = $WebSearchURL . urlencode('\'' . $_POST["search_query"] . '\'') . '%27&$format=json';
                                    $response = file_get_contents($request, 0, $context);
                                    $jsonobj = json_decode($response);
                                    foreach ($jsonobj->d->results as $value) {
                                        echo "<div class='well'>";
                                        echo "<a target=\"_blank\" href='" . $value->Url . "'>" . $value->Title . "</a>";
                                        echo "<p>" . $value->Description . "</p>";
                                        echo "<span>Publisher: " . $value->Source . "</span><br>";
                                        echo "<span>Date: " . $value->Date . "</span>";
                                        echo '</div>';
                                    }
                                }
                                ?>
                            </div>
                        </div><!-- tab content -->
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<script src="js/jquery-2.2.1.min.js"></script>
<script src="js/bootstrap.min.js"></script>
<script src="https://gitcdn.github.io/bootstrap-toggle/2.2.2/js/bootstrap-toggle.min.js"></script>

<script src="https://code.highcharts.com/stock/highstock.js"></script>
<script src="https://code.highcharts.com/stock/modules/exporting.js"></script>
<script src="https://www.google.com/jsapi"></script>
<script src="//code.jquery.com/ui/1.11.4/jquery-ui.js"></script>

<script>
    // global variable
    var myStorage = localStorage;
    var currSymbol = "";
    var currCompanyName = "";
    var currStockPrice = "";
    var currChange = "";
    var currMarketCap = "";
    var currFavorite = "0";


    $(document).ready(function () {
        $("#search_query").autocomplete({
            source: function (request, response) {
                $.ajax({
                    type: 'get',
                    url: './search.php?search_query=' + request.term + '&json1callback=?',
                    dataType: "jsonp",
                    success: function (json) {
                        if (json) {
                            data = $.map(json, function (a, index) {
                                return a;
                            });
                            len = data.length;
                            response($.map(data, function (item, index) {
                                var name = item.Name;
                                var symbol = item.Symbol;
                                var exc = item.Exchange;
                                return {
                                    label: item.Symbol + '-' + item.Name + '(' + item.Exchange + ')',
                                    value: item.Symbol
                                }
                            }));
                        } else {
                            len = 0;
                            data = null;
                            return {};
                        }
                    }

                });
            }
        });

        $(window).trigger('resize');

        var favoristListTable = $('table#favoriteList');
        favoristListTable.html("");
        favoristListTable.append('<tr><th>Symbol</th><th>Company Name</th><th>Stock Price</th><th>Change</th><th>Market Cap</th></tr>');

        for (var key in myStorage) {
            var rowContent = myStorage.getItem(key).split(",");

            favoristListTable.append('<tr><td>' + key + '</td><td>' +
                rowContent[0] + '</td><td>' + rowContent[1] + '</td><td>' +
                rowContent[2] + '</td><td> ' + rowContent[3] + '</td></tr>');
        }
    });

    // Facebook
    window.fbAsyncInit = function () {
        FB.init({
            appId: '154164808311370',
            xfbml: true,
            version: 'v2.5'
        });
    };

    var fbTitle = "Current Weather in ";
    var fbIcon = "http://chart.finance.yahoo.com/t?s=AA&lang=en-US&width=400&height=300";
    var fbDescription = "This is description";
    var fbCaption = "This is Caption";

    (function (d, s, id) {
        var js, fjs = d.getElementsByTagName(s)[0];
        if (d.getElementById(id)) {
            return;
        }
        js = d.createElement(s);
        js.id = id;
        js.src = "//connect.facebook.net/en_US/sdk.js";
        fjs.parentNode.insertBefore(js, fjs);
    }(document, 'script', 'facebook-jssdk'));

    function fbShare() {
        FB.ui({
            method: 'feed',
            name: decodeURIComponent(fbTitle),
            link: decodeURIComponent("http://forecast.io/"),
            picture: decodeURIComponent(fbIcon),
            caption: decodeURIComponent(fbCaption),
            description: decodeURIComponent(fbDescription)
        }, function (response) {
            if (response && !response.error_code) {
                alert('Posted Successfully');
            } else {
                alert('Not Posted');
            }
        });
    }
    // End of FaceBook

    // Favorite List
    function addFavoriteList() {

        $("button #favorite").css('color', 'yellow');

        currSymbol = $("table #symbol").html();
        currChange = $("table #change").html();
        currMarketCap = $("table #marketCap").html();
        currStockPrice = $("table #price").html();
        currCompanyName = $("table #companyName").html();

        myStorage.setItem(currSymbol, [currCompanyName, currStockPrice, currChange, currMarketCap]);

        var favoristListTable = $('table#favoriteList');
        favoristListTable.html("");
        favoristListTable.append('<tr><th>Symbol</th><th>Company Name</th><th>Stock Price</th><th>Change</th><th>Market Cap</th></tr>');

        for (var key in myStorage) {
            var rowContent = myStorage.getItem(key).split(",");

            favoristListTable.append('<tr><td>' + key + '</td><td>' +
                rowContent[0] + '</td><td>' + rowContent[1] + '</td><td>' +
                rowContent[2] + '</td><td> ' + rowContent[3] + '</td></tr>');
        }
    }
    // End of Favorite List

    // Draw Chart

    $("form").submit(function (event) {
        if ($("input").val() === "") {
            alert("Empty input");
            event.preventDefault();
        }
    });



</script>



<script>
    /**
     * Version 2.0
     */
    var Markit = {};
    /**
     * Define the InteractiveChartApi.
     * First argument is symbol (string) for the quote. Examples: AAPL, MSFT, JNJ, GOOG.
     * Second argument is duration (int) for how many days of history to retrieve.
     */
    Markit.InteractiveChartApi = function(symbol,duration){
        this.symbol = symbol.toUpperCase();
        this.duration = duration;
        this.PlotChart();
    };

    Markit.InteractiveChartApi.prototype.PlotChart = function(){

        var params = {
            parameters: JSON.stringify( this.getInputParams() )
        }

        //Make JSON request for timeseries data
        $.ajax({
            beforeSend:function(){
                $("#chartDemoContainer").text("Loading chart...");
            },
            data: params,
            url: "http://dev.markitondemand.com/Api/v2/InteractiveChart/jsonp",
            dataType: "jsonp",
            context: this,
            success: function(json){
                //Catch errors
                if (!json || json.Message){
                    console.error("Error: ", json.Message);
                    return;
                }
                this.render(json);
            },
            error: function(response,txtStatus){
                console.log(response,txtStatus)
            }
        });
    };

    Markit.InteractiveChartApi.prototype.getInputParams = function(){
        return {
            Normalized: false,
            NumberOfDays: this.duration,
            DataPeriod: "Day",
            Elements: [
                {
                    Symbol: this.symbol,
                    Type: "price",
                    Params: ["ohlc"] //ohlc, c = close only
                },
                {
                    Symbol: this.symbol,
                    Type: "volume"
                }
            ]
            //,LabelPeriod: 'Week',
            //LabelInterval: 1
        }
    };

    Markit.InteractiveChartApi.prototype._fixDate = function(dateIn) {
        var dat = new Date(dateIn);
        return Date.UTC(dat.getFullYear(), dat.getMonth(), dat.getDate());
    };

    Markit.InteractiveChartApi.prototype._getOHLC = function(json) {
        var dates = json.Dates || [];
        var elements = json.Elements || [];
        var chartSeries = [];

        if (elements[0]){

            for (var i = 0, datLen = dates.length; i < datLen; i++) {
                var dat = this._fixDate( dates[i] );
                var pointData = [
                    dat,
                    elements[0].DataSeries['open'].values[i],
                    elements[0].DataSeries['high'].values[i],
                    elements[0].DataSeries['low'].values[i],
                    elements[0].DataSeries['close'].values[i]
                ];
                chartSeries.push( pointData );
            };
        }
        return chartSeries;
    };

    Markit.InteractiveChartApi.prototype._getVolume = function(json) {
        var dates = json.Dates || [];
        var elements = json.Elements || [];
        var chartSeries = [];

        if (elements[1]){

            for (var i = 0, datLen = dates.length; i < datLen; i++) {
                var dat = this._fixDate( dates[i] );
                var pointData = [
                    dat,
                    elements[1].DataSeries['volume'].values[i]
                ];
                chartSeries.push( pointData );
            };
        }
        return chartSeries;
    };

    Markit.InteractiveChartApi.prototype.render = function(data) {
        //console.log(data)
        // split the data set into ohlc and volume
        var ohlc = this._getOHLC(data),
            volume = this._getVolume(data);

        // set the allowed units for data grouping
        var groupingUnits = [[
            'week',                         // unit name
            [1]                             // allowed multiples
        ], [
            'month',
            [1, 2, 3, 4, 6]
        ]];

        // create the chart
        $('#chartDemoContainer').highcharts('StockChart', {

            rangeSelector: {

                selected: 0,
                allButtonsEnabled: true,
                inputEnabled: false,
                buttons: [{
                    type: 'week',
                    count: 1,
                    text: '1w'
                },
                    {
                        type: 'month',
                        count: 1,
                        text: '1m'
                    }, {
                        type: 'month',
                        count: 3,
                        text: '3m'
                    }, {
                        type: 'month',
                        count: 6,
                        text: '6m'
                    }, {
                        type: 'ytd',
                        text: 'YTD'
                    }, {
                        type: 'year',
                        count: 1,
                        text: '1y'
                    }, {
                        type: 'all',
                        text: 'All'
                    }]
            },

            title: {
                text: this.symbol + ' Historical Price'
            },

            yAxis: [{
                title: {
                    text: 'Stock Value'
                },
                // height: 300,
                // lineWidth: 2
                min: 0
            }],

            series : [{
                name : 'AAPL Stock Price',
                data : ohlc,
                type : 'area',
                threshold : null,
                tooltip : {
                    valueDecimals : 2
                },
                fillColor : {
                    linearGradient : {
                        x1: 0,
                        y1: 0,
                        x2: 0,
                        y2: 1
                    },
                    stops : [
                        [0, Highcharts.getOptions().colors[0]],
                        [1, Highcharts.Color(Highcharts.getOptions().colors[0]).setOpacity(0).get('rgba')]
                    ]
                }
            }],
        });
    };

    function drawChart(Company) {
        new Markit.InteractiveChartApi(Company, 3650);
    }

    $('#tabB').click(function () {
        $('#chartDemoContainer').highcharts().reflow();
    });

</script>

<?php
if ($_SERVER["REQUEST_METHOD"] == "POST") {
    echo "<script>drawChart($('input').val());$('button#prev').removeAttr('disabled');$('button#next').removeAttr('disabled')</script>";
}
?>

</body>
</html>