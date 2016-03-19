<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <title>Stock Search</title>
    <style>
        span.search_head {
            font-size: 24px;
            padding: 50px 50px 50px 50px;
        }

        table.search, table.search th, table.search td {
            background-color: rgb(243, 243, 243);
        }

        table.search {
            border-collapse: collapse;
            border: 1px solid black;
            padding: 20px;
            margin-bottom: 50px;
        }

        table.search th {
            border-bottom: 1px solid black;
            border-collapse: collapse;
        }

        table.info, table.info th, table.info td {
            /*align: center;*/
            background-color: rgb(243, 243, 243);
            border-width: 1px;
            border-style: solid;
            border-color: rgb(0, 0, 0);
            border-collapse: collapse;
        }
    </style>
    <script type="text/javascript">
        function check_validation() {
            if (document.getElementById("textbox").value == "") {
                alert("The search query is empty.")
            }
        }
        function clear_form() {
            document.getElementById("textbox").value = "";
            if(document.getElementById("search_result")) {
                document.getElementById("search_result").remove();
            }
            if(document.getElementById("info_result")) {
                document.getElementById("info_result").remove();
            }
        }
    </script>
</head>
<body>

<div id="search" align="center">
    <form action="<?php echo htmlspecialchars($_SERVER["PHP_SELF"]); ?>" method="post" name="search_form">
        <table class="search">
            <th colspan="2">
               <span class="search_head">Stock Search<span>
            </th>
            <tr>
                <td>Company name or Symbol:</td>
                <td>
                    <input type="search" name="textbox" id="textbox" required pattern=".*\S{1}.*"
                           value=<?php
                    if ($_SERVER["REQUEST_METHOD"] == "POST") {
                        echo $_POST["textbox"];
                    }
                    if ($_SERVER["REQUEST_METHOD"] == "GET" and isset($_GET["textbox"])) {
                        echo $_GET["textbox"];
                    }
                    ?>>
                </td>
            </tr>
            <tr>
                <td></td>
                <td>
                    <button type="submit" name="Submit" value="submit" onclick="check_validation()">Submit</button>
                    <button type="button" name="Clear" value="clear" onclick="clear_form()">Clear</button>
                </td>
            </tr>
            <th colspan="2">
                <a href="http://www.markit.com/product/markit-on-demand">Powered by Market on Demand<a>
            </th>

        </table>
    </form>
</div>

<div id="infomation">
    <form action="<?php echo htmlspecialchars($_SERVER["PHP_SELF"]); ?>" method="get" name="info_form">

        <?php
        $company = "";
        if ($_SERVER["REQUEST_METHOD"] == "POST") {
            $company = $_POST["textbox"];

            if ($company != "") {
                getresult($company);
            }
        }
        if ($_SERVER["REQUEST_METHOD"] == "GET") {
            if (array_key_exists('symbol', $_GET)) {
                $url_moreinfo = "http://dev.markitondemand.com/MODApis/Api/v2/Quote/json?symbol=" . $_GET["symbol"];
                $content = file_get_contents($url_moreinfo);
                $more_info = json_decode($content);
                show_more_info($more_info);
            }
        }

        function getresult($company) {
            $url = "http://dev.markitondemand.com/MODApis/Api/v2/Lookup/xml?input=" . $company;
            $result = file_get_contents($url);
            $xml_doc = simplexml_load_string($result);
            echo "<div align='center' id='search_result'><table class='info'>";
            if (sizeof($xml_doc->children()) != 0) {
                echo "<tr><th>Name</th><th>Symbol</th><th>Exchange</th><th>Details</th></tr></th>";
                foreach ($xml_doc->children() as $search_result) {
                    echo "<tr>";
                    echo "<td>" . $search_result->Name . "</td>";
                    echo "<td>" . $search_result->Symbol . "</td>";
                    echo "<td>" . $search_result->Exchange . "</td>";
                    echo "<td><a href=\"homework6.php?symbol=" . $search_result->Symbol . "&textbox=" . $company. "\">More Info</a></td>";
                    echo "</tr>";

                }

            } else {
                echo "<tr><td><span style='font-size: 23px; margin: 30px'>No Record has been foun</span></td></tr>";
            }
            echo "</table></div>";
        }

        function show_more_info($more_info) {
            echo "<div align='center' id='info_result'><table class='info'>";
            if ($more_info->{'Status'} == "SUCCESS") {
                echo "<tr><td>Name</td><td>" . $more_info->{'Name'} . "</td></tr>";
                echo "<tr><td>Symbol</td><td>" . $more_info->{'Symbol'} . "</td></tr>";
                echo "<tr><td>Last Price</td><td>" . $more_info->{'LastPrice'} . "</td></tr>";
                echo "<tr><td>Change</td><td>" . number_format($more_info->{'Change'}, 2, '.', '') . get_img($more_info->{'Change'}) . "</td></tr>";
                echo "<tr><td>Change Percent</td><td>" . number_format($more_info->{'ChangePercent'}, 2, '.', '') . "%" . get_img($more_info->{'ChangePercent'}) . "</td></tr>";
                echo "<tr><td>Timestamp</td><td>" . date("Y-m-d g:i A T", strtotime($more_info->{'Timestamp'})) . "</td></tr>";
                echo "<tr><td>MarketCap</td><td>" . number_format($more_info->{'MarketCap'} / 1000000000, 2, '.', '') . "B" . "</td></tr>";
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
            echo "</table></div>";
        }

        function get_img($value) {
            $arrow_img = "<img width='12px' height='12px' src='";
            if ($value > 0) {
                $arrow_img .= "http://cs-server.usc.edu:45678/hw/hw6/images/Green_Arrow_Up.png'>";
            } else {
                $arrow_img .= "http://cs-server.usc.edu:45678/hw/hw6/images/Red_Arrow_Down.png'>";
            }
            return $arrow_img;
        }
        ?>
    </form>
</div>
</body>
</html>
