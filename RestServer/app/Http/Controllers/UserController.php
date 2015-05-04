<?php namespace App\Http\Controllers;

use Auth;
use App\User;

class UserController extends Controller {

    /**
     * Instantiate a new UserController instance.
     */
    public function __construct()
    {
        $this->middleware('auth');
    }

    public function showProfile()
    {
    	$attributes = Auth::user()['attributes'];
    	// var_dump($attributes);

    	$data = json_encode(array('name' => $attributes['name'], 
    								'email' => $attributes['email'],
    								'bonuspoints' => $attributes['bonuspoints'],
    								'bonusdays' => $attributes['bonusdays'],
    								'visible' => $attributes['visible']), JSON_FORCE_OBJECT);

    	return $data;
    }

    public function showScoreboard(){
    	return User::where('visible','=',1)->get();
    }

}