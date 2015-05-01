<?php namespace App;

use Illuminate\Database\Eloquent\Model;

class Station extends Model {

	/**
	 * The attributes excluded from the model's JSON form.
	 *
	 * @var array
	 */
	protected $hidden = ['banking', 'bonus', 'contract_name', 'last_update', 'created_at', 'updated_at'];

}
