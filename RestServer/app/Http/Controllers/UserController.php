<?php namespace App\Http\Controllers;

use Auth;
use App\User;

class UserController extends Controller {

    /**
     * Instantiate a new UserController instance.
     */
    public function __construct()
    {
    }

    public function showProfile($email)
    {
        return User::where('email','=',$email)->get()[0];
    }

    public function showScoreboard(){
    	return User::where('visible','=',1)->get();
    }

}