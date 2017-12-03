<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Movies</title>
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.2.1/jquery.min.js"></script>
    <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>
    <script src="https://s.codepen.io/assets/libs/modernizr.js" type="text/javascript"></script>
    <script src="//cdnjs.cloudflare.com/ajax/libs/bootstrap-validator/0.4.5/js/bootstrapvalidator.min.js" type="text/javascript"></script>
    <script
            src="https://maps.googleapis.com/maps/api/js?libraries=places&sensor=true&language=en&key=AIzaSyCM4ZZEHZuIsF-LfxbooRXcsA487D269cc"></script>

</head>
<body>

    <h2> Do you like those recommended objects? Do they match your interests?</h2>
    <h3>Please, give a rating of your satisfation</h3>
    <p><form action="Evaluation" method="post">
    <input type="radio" name="recommender_rating" value="1" required>Poor, I am not interested in those objects</input><br>
    <input type="radio" name="recommender_rating" value="2">Fair</input><br>
    <input type="radio" name="recommender_rating" value="3">Average, I am interested in some objects but I really don't like some others</input><br>
    <input type="radio" name="recommender_rating" value="4">Good</input><br>
    <input type="radio" name="recommender_rating" value="5">Excellent: They exactly match my interests!</input>

    <br>
    <br>
    <h2>This recommender helped me discover a new item in the list I didn't know before</h2>
    <input type="radio" name="novelty" value="1" required>Strongly disagree</input>
    <input type="radio" name="novelty" value="2">Disagree</input>
    <input type="radio" name="novelty" value="3">Neither disagree nor agree</input>
    <input type="radio" name="novelty" value="4">Agree</input>
    <input type="radio" name="novelty" value="5">Strongly agree</input>

    <br>
    <br>
    <h2>This recommender helped me discovered a new item in the list and I really want to watch it live.</h2>
    <input type="radio" name="serendipity" value="1" required>Strongly disagree</input>
    <input type="radio" name="serendipity" value="2">Disagree</input>
    <input type="radio" name="serendipity" value="4">Neither Disagree nor agree</input>
    <input type="radio" name="serendipity" value="4">Agree</input>
    <input type="radio" name="serendipity" value="5">Strongly Agree</input>

    <h2>The items recommended to me are similar to each other, they are not diverse</h2>
    <input type="radio" name="diversity" value="5" required>Strongly disagree</input>
    <input type="radio" name="diversity" value="4">Disagree</input>
    <input type="radio" name="diversity" value="3">Neither Disagree nor agree</input>
    <input type="radio" name="diversity" value="2">Agree</input>
    <input type="radio" name="diversity" value="1">Strongly Agree</input>

        <h2>The items recommended to me are obvious, they are not original</h2>
        <input type="radio" name="obvious" value="5" required>Strongly disagree</input>
        <input type="radio" name="obvious" value="4">Disagree</input>
        <input type="radio" name="obvious" value="3">Neither Disagree nor agree</input>
        <input type="radio" name="obvious" value="2">Agree</input>
        <input type="radio" name="obvious" value="1">Strongly Agree</input>

        <h2>How many places of the route you would not want to visit?</h2>
        <input type="text" name="venues" required>




        <div style="position:absolute; top:700px"><input type="submit" value="SUBMIT"/></div>


</form></p>
</body>
</html>