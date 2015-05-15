<?php namespace App\Http\Controllers;

use Auth;
use App\User;
use Input;

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

    public function scan(){
        $code = Input::get('code');
        $email = Input::get('email');

        //if( $code != (integer)$code )
            //echo 'not a integer';

        // afleidingsmechanisme nodig die code valideert, fietslocatie bepaalt en zo het aantal bonuspunten toekent
        // voor nu gewoon +1

        $users = User::all();

        $current_points = 0;
        foreach($users as $user){
            if($user->email == "$email")
                $current_points = $user->bonuspoints;
        }
        
        User::where('email', '=', $email)->update(array('bonuspoints' => $current_points+1));
    }

}