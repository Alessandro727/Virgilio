<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!-- <!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd"> -->
<!DOCTYPE html>
<html lang="en">
<head>
    <title>Register Form</title>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.2.1/jquery.min.js"></script>
    <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>
    <script src="https://s.codepen.io/assets/libs/modernizr.js" type="text/javascript"></script>
    <script src="//cdnjs.cloudflare.com/ajax/libs/bootstrap-validator/0.4.5/js/bootstrapvalidator.min.js" type="text/javascript"></script>
    <script
            src="https://maps.googleapis.com/maps/api/js?libraries=places&sensor=true&language=en&key=AIzaSyCM4ZZEHZuIsF-LfxbooRXcsA487D269cc"></script>


    <style>
    #success_message{ display: none;}

    /* active */




    .buttonBlue {
        background: #4a89dc;
        text-shadow: 1px 1px 0 rgba(39, 110, 204, .5);
    }

    .buttonBlue:hover { background: #357bd8; }

    .button {
        position: relative;
        display: inline-block;
        padding: 12px 24px;
        margin: .3em 0 1em 0;
        width: 100%;
        vertical-align: middle;
        color: #fff;
        font-size: 16px;
        line-height: 20px;
        -webkit-font-smoothing: antialiased;
        text-align: center;
        letter-spacing: 1px;
        border: 0;
        border-bottom: 2px solid #3160B6;
        cursor: pointer;
        -webkit-transition:all 0.15s ease;
        transition: all 0.15s ease;
    }
    .button:focus { outline: 0; }




    </style>
</head>
<body>



<div class="container">

    <form class="well form-horizontal" action="Register" method="post"  id="contact_form">
        <fieldset>

            <!-- Form Name -->
            <legend><center><h2><b>Registration Form</b></h2></center></legend><br>

            <% if (request.getAttribute("error") != null) { %>
            <div>
                <span style="color: red"><%= request.getAttribute("error") %></span>
            </div>
            <% } %>
            <div class="form-group">
                <label class="col-md-4 control-label">Username</label>
                <div class="col-md-4 inputGroupContainer">
                    <div class="input-group">
                        <span class="input-group-addon"><i class="glyphicon glyphicon-user"></i></span>
                        <input id="txtUsername" name="txtUsername" placeholder="Username" class="form-control" type="text">
                    </div>
                </div>
            </div>

            <!-- Text input-->

            <div class="form-group">
                <label class="col-md-4 control-label" >Password</label>
                <div class="col-md-4 inputGroupContainer">
                    <div class="input-group">
                        <span class="input-group-addon"><i class="glyphicon glyphicon-user"></i></span>
                        <input id="txtPassword" name="txtPassword" placeholder="Password" class="form-control" type="password">
                    </div>
                </div>
            </div>

            <div class="form-group">
                <label class="col-md-4 control-label" >Confirm Password</label>
                <div class="col-md-4 inputGroupContainer">
                    <div class="input-group">
                        <span class="input-group-addon"><i class="glyphicon glyphicon-user"></i></span>
                        <input name="confirm_password" placeholder="Confirm Password" class="form-control"  type="password">
                    </div>
                </div>
            </div>

            <!-- Text input-->


            <div class="form-group">
                <label class="col-md-4 control-label">Role</label>
                <div class="col-md-4 selectContainer">
                    <div class="input-group">
                        <span class="input-group-addon"><i class="glyphicon glyphicon-list"></i></span>
                        <select name="ddlRole" class="form-control selectpicker">
                            <option value="">Select your Role</option>
                            <option>Student</option>
                            <option>Teacher</option>
                            <option >Employee</option>
                            <option >Freelancer</option>
                            <option >Currently Unoccupied</option>
                        </select>
                    </div>
                </div>
            </div>

            <!-- Text input-->





            <!-- Text input-->
            <div class="form-group">
                <label class="col-md-4 control-label">Age</label>
                <div class="col-md-4 inputGroupContainer">
                    <div class="input-group">
                        <span class="input-group-addon"><i class="glyphicon glyphicon-user"></i></span>
                        <input name="txtAge" placeholder="Age" class="form-control"  type="text">
                    </div>
                </div>
            </div>


            <!-- Text input-->

            <div class="form-group">
                <label class="col-md-4 control-label">Gender</label>
                <div class="col-md-4 inputGroupContainer">
                    <div class="input-group" style="top: 8px">
                        &nbsp;&nbsp;
                        <input type="radio" name="rdGender" value="male">Male
                        &nbsp;&nbsp; <input type="radio" name="rdGender" value="female">Female
                    </div>
                </div>
            </div>



            <!-- Text input-->
            <div class="form-group">
                <label class="col-md-4 control-label">Residence</label>
                <div class="col-md-4 inputGroupContainer">
                    <div class="input-group">
                        <span class="input-group-addon"><i class="glyphicon glyphicon-road"></i></span>
                        <input id="autocomplete" name="residence"
                               placeholder="Enter your address"
                               onFocus="enableDisableInput(false)" class="form-control" type="text"></input>
                    </div>
                </div>
            </div>

            <!-- Select Basic -->

            <!-- Success message -->
            <div class="alert alert-success" role="alert" id="success_message">Success <i class="glyphicon glyphicon-thumbs-up"></i> Success!.</div>

            <!-- Button -->
            <div class="form-group" id="button">
                <label class="col-md-4 control-label"></label>
                <div class="col-md-4"><br>
                    &nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp<button type="submit" class="button buttonBlue" >&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbspSUBMIT <span class="glyphicon glyphicon-send"></span>&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp<div class="ripples buttonRipples"><span class="ripplesCircle"></span></div></button>
                </div>
            </div>

        </fieldset>
    </form>

</div><!-- /.container -->

</body>

<script>
    $(document).ready(function() {
        $('#contact_form').bootstrapValidator({
            // To use feedback icons, ensure that you use Bootstrap v3.1.0 or later
            feedbackIcons: {
                valid: 'glyphicon glyphicon-ok',
                invalid: 'glyphicon glyphicon-remove',
                validating: 'glyphicon glyphicon-refresh'
            },
            fields: {

                txtUsername: {
                    validators: {
                        stringLength: {
                            min: 2,
                        },
                        notEmpty: {
                            message: 'Please enter your Username'
                        }
                    }
                },
                txtPassword: {
                    validators: {
                        stringLength: {
                            min: 2,
                        },
                        notEmpty: {
                            message: 'Please enter your Password'
                        }
                    }
                },
                confirm_password: {
                    validators: {
                        stringLength: {
                            min: 2,
                        },
                        notEmpty: {
                            message: 'Please confirm your Password'
                        }
                    }
                },
                txtAge: {
                    validators: {
                        notEmpty: {
                            message: 'Please enter your Age'
                        },
                        integer: {
                            message: 'The value is not a number'
                        }
                    }
                },
                ddlRole: {
                    validators: {
                        notEmpty: {
                            message: 'Please select your Role'
                        }
                    }
                },
            }

         })
            .on('success.form.bv', function(e) {
                $('#success_message').slideDown({ opacity: "show" }, "slow") // Do something ...
                $('#contact_form').data('bootstrapValidator').resetForm();

                // Prevent form submission
                e.preventDefault();

                // Get the form instance
                var $form = $(e.target);

                // Get the BootstrapValidator instance
                var bv = $form.data('bootstrapValidator');

                // Use Ajax to submit form data
                $.post($form.attr('action'), $form.serialize(), function(result) {
                    console.log(result);
                }, 'json');
            });
    });

    function initialize() {

        var input = document.getElementById('autocomplete');
        var autocomplete = new google.maps.places.Autocomplete(input);
    }

    function enableDisableInput(value) {
        document.getElementById('btnRegister').disabled = value;
    }

    google.maps.event.addDomListener(window, 'load', initialize);

</script>

</html>
