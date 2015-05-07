<?php namespace App\Http\Controllers;

use GuzzleHttp\Client;
use App\Station;

class StationController extends Controller {

	/*
	|--------------------------------------------------------------------------
	| Station Controller
	|--------------------------------------------------------------------------
	|
	| This controller returns Villo!-stations
	|
	*/

	/**
	 * Create a new controller instance.
	 *
	 * @return void
	 */
	public function __construct()
	{
		//$this->middleware('guest');
	}

	/**
	 * Returns list of stations
	 *
	 * @return string
	 */
	public function getStations()
	{
		$this->update();

		return Station::all();
	}

	public function update()
	{
		$client = new Client();

		$response = $client->get('http://opendata.brussel.be/api/records/1.0/search?dataset=villo-stations-beschikbaarheid-in-real-time&rows=1000&facet=bonus&facet=status&facet=contract_name&facet=banking');
		$json = $response->json();
		$stations = $json['records'];

		foreach ($stations as $station)
		{
			Station::where('number', '=', $station['fields']['number'])
			->update(array('bonus' => $station['fields']['bonus'],
						    'status' => $station['fields']['status'],
						    'contract_name' => $station['fields']['contract_name'],
						    'bike_stands' => $station['fields']['bike_stands'],
						    'available_bike_stands' => $station['fields']['available_bike_stands'],
						   	'available_bikes' => $station['fields']['available_bikes'],
						    'last_update' => $station['fields']['last_update'],
						    'bonuspoints' => $this->calculateBonus($station['fields']['bike_stands'], $station['fields']['available_bikes'])
		  	));
		}  
	}

	/**
	 * Returns bonuspoints
	 *	 
	 * @param integer total bike stands
	 * @param integer available bike stands
	 * @param integer available bikes
	 *
	 * @return int
	 */
	public function calculateBonus($bike_stands, $available_bikes)
	{
		return $available_bikes>$bike_stands*3/4 ? 0 : intval(10 - $available_bikes/$bike_stands*10);
	}



}
