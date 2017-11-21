<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <title>Category Choose</title>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">

    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.2.1/jquery.min.js"></script>

    <style>
        ul {
            list-style-type: none;
            margin-left: 0px;
        }

        li {
            display: inline-block;
        }

        input[type="checkbox"][id^="cb"] {
            display: none;
        }

        #body {
            height: 100%;
            width: 100%;
        }
        #div {
            width: 100%;
        }

        label {
            border: 1px solid #fff;
            padding: 10px;
            display: block;
            position: relative;
            margin: 10px;
            cursor: pointer;
        }

        label:before {
            background-color: white;
            color: white;
            content: " ";
            display: block;
            border-radius: 50%;
            border: 1px solid dodgerblue;
            position: absolute;
            top: -5px;
            left: -5px;
            width: 25px;
            height: 25px;
            text-align: center;
            line-height: 28px;
            transition-duration: 0.4s;
            transform: scale(0);
        }

        label img {
            height: 200px;
            width: 280px;
            transition-duration: 0.2s;
            transform-origin: 50% 50%;
        }

        :checked + label {
            border-color: #ddd;
        }

        :checked + label:before {
            content: "\2713";
            background-color: dodgerblue;
            transform: scale(1.2);
        }

        :checked + label img {
            transform: scale(0.9);
            box-shadow: 0 0 5px #333;
            z-index: -1;
        }

        .button {
            position: relative;
            display: inline-block;
            padding: 12px 24px;
            margin: .3em 0 1em 0;
            width: 20%;
            vertical-align: middle;
            color: #fff;
            font-size: 16px;
            line-height: 20px;
            -webkit-font-smoothing: antialiased;
            text-align: center;
            letter-spacing: 1px;
            background: transparent;
            border: 0;
            border-bottom: 2px solid #3160B6;
            cursor: pointer;
            -webkit-transition:all 0.15s ease;
            transition: all 0.15s ease;
            left: -7.8px;
        }
        .button:focus { outline: 0; }


        /* Button modifiers */

        .buttonBlue {
            background: #4a89dc;
            text-shadow: 1px 1px 0 rgba(39, 110, 204, .5);
        }

        .buttonBlue:hover { background: #357bd8; }

    </style>

</head>
<body id="body">
<form id="form" action="CategoryImages" method="post">
    <input type="hidden" id="hidden_array" name="hiddenArray" >
    <div id="div">
        <h2 style="text-align: center;">Choose 5 types of places you would like to visit on your travels.</h2>
        <ul>
            <li><input type="checkbox" id="cb16" />
                <label class="" id="museum1" name="museum1" for="cb16"><img src="images/museum1.jpg" /></label>
            </li>
            <li><input type="checkbox" id="cb17" />
                <label class="" id="art2" name="art2" for="cb17" class=""><img  src="images/art2.jpg"  /></label>
            </li>
            <li><input type="checkbox" id="cb18" />
                <label class="" id="food3" name="food3" for="cb18"><img src="images/food3.jpg" /></label>
            </li>
            <li><input type="checkbox" id="cb19" />
                <label class="" id="sport2" name="sport2" for="cb19"><img src="images/sport2.jpg" /></label>
            </li>
            <li><input type="checkbox" id="cb20" />
                <label class="" id="chiesa2" name="chiesa2" for="cb20"><img src="images/chiesa2.jpg" /></label>
            </li>
            <li><input type="checkbox" id="cb21" />
                <label class="" id="entertaiment2" name="entertaiment2" for="cb21"><img src="images/entertaiment2.jpg" /></label>
            </li>
            <li><input type="checkbox" id="cb22" />
                <label class="" id="monument2" name="monument2" for="cb22" class=""><img  src="images/monument2.jpg"  /></label>
            </li>
            <li><input type="checkbox" id="cb23" />
                <label class="" id="shop1" name="shop1" for="cb23"><img src="images/shop1.jpg" /></label>
            </li>
            <li><input type="checkbox" id="cb24" />
                <label class="" id="entertaiment3" name="entertaiment3" for="cb24"><img src="images/entertaiment3.jpg" /></label>
            </li>
            <li><input type="checkbox" id="cb25" />
                <label class="" id="food2" name="food2" for="cb25"><img src="images/food2.jpg" /></label>
            </li>
            <li><input type="checkbox" id="cb26" />
                <label class="" id="shop2" name="shop2" for="cb26"><img src="images/shop2.jpg" /></label>
            </li>
            <li><input type="checkbox" id="cb27" />
                <label class="" id="outdoors2" name="outdoors2" for="cb27" class=""><img  src="images/outdoors2.jpg"  /></label>
            </li>
            <li><input type="checkbox" id="cb28" />
                <label class="" id="monument3" name="monument3" for="cb28"><img src="images/monument3.jpg" /></label>
            </li>
            <li><input type="checkbox" id="cb29" />
                <label class="" id="sport3" name="sport3" for="cb29"><img src="images/sport3.jpg" /></label>
            </li>
            <li><input type="checkbox" id="cb30" />
                <label class="" id="outdoors3" name="outdoors3" for="cb30"><img src="images/outdoors3.jpg" /></label>
            </li>
            <li><input type="checkbox" id="cb1" />
                <label class="" id="chiesa1" name="chiesa1" for="cb1"><img src="images/chiesa1.jpg" /></label>
            </li>
            <li><input type="checkbox" id="cb2" />
                <label class="" id="entertaiment1" name="entertaiment1" for="cb2" class=""><img  src="images/entertaiment1.jpg"  /></label>
            </li>
            <li><input type="checkbox" id="cb3" />
                <label class="" id="sport1" name="sport1" for="cb3"><img src="images/sport1.jpg" /></label>
            </li>
            <li><input type="checkbox" id="cb4" />
                <label class="" id="night3" name="night3" for="cb4"><img src="images/night3.jpg" /></label>
            </li>
            <li><input type="checkbox" id="cb5" />
                <label class="" id="museum2" name="museum2" for="cb5"><img src="images/museum2.jpg" /></label>
            </li>
            <li><input type="checkbox" id="cb6" />
                <label class="" id="chiesa3" name="chiesa3" for="cb6"><img src="images/chiesa3.jpg" /></label>
            </li>
            <li><input type="checkbox" id="cb7" />
                <label class="" id="shop3" name="shop3" for="cb7" class=""><img  src="images/shop3.jpg"  /></label>
            </li>
            <li><input type="checkbox" id="cb8" />
                <label class="" id="museum3" name="museum3" for="cb8"><img src="images/museum3.jpg" /></label>
            </li>
            <li><input type="checkbox" id="cb9" />
                <label class="" id="food1" name="food1" for="cb9"><img src="images/food1.jpg" /></label>
            </li>
            <li><input type="checkbox" id="cb10" />
                <label class="" id="outdoors1" name="outdoors1" for="cb10"><img src="images/outdoors1.jpg" /></label>
            </li>
            <li><input type="checkbox" id="cb11" />
                <label class="" id="monument1" name="monument1" for="cb11"><img src="images/monument1.jpg" /></label>
            </li>
            <li><input type="checkbox" id="cb12" />
                <label class="" id="art3" name="art3" for="cb12" class=""><img  src="images/art3.jpg"  /></label>
            </li>
            <li><input type="checkbox" id="cb13" />
                <label class="" id="night1" name="night1" for="cb13"><img src="images/night1.jpg" /></label>
            </li>
            <li><input type="checkbox" id="cb14" />
                <label class="" id="art1" name="art1" for="cb14"><img src="images/art1.jpg" /></label>
            </li>
            <li><input type="checkbox" id="cb15" />
                <label class="" id="night2" name="night2" for="cb15"><img src="images/night2.jpg" /></label>
            </li>
        </ul>
    </div>
    <button style="margin:auto; display:block;" id="btnLogin" name="btnLogin" value="CategoryImages" type="submit" class="button buttonBlue">Done
        <div class="ripples buttonRipples"><span class="ripplesCircle"></span></div>
    </button>
</form>
<div>&nbsp</div>
<div>&nbsp</div>
</body>
<script>

    var w = $('#div').width()

    $('label img').width(w/5-21.7*3)


    $('label').click(function () {
        if ($(this).data('selected')) {
            $(this).removeClass('selected');
            $(this).data('selected', false);
        } else {
            $(this).addClass('selected');
            $(this).data('selected', true);
        }
        var selectedImageArray = [];
        $('label').each(function () {
            if ($(this).data('selected')) {
                selectedImageArray.push(this.id);
            }
        });
        console.log(selectedImageArray)
        $("#hidden_array").val(selectedImageArray);
    });




</script>
</html>
