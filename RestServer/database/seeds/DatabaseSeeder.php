<?php

use Illuminate\Database\Seeder;
use Illuminate\Database\Eloquent\Model;
use App\User;
use App\Station;
use GuzzleHttp\Client;

class DatabaseSeeder extends Seeder {

	/**
	 * Run the database seeds.
	 *
	 * @return void
	 */
	public function run()
	{
		Model::unguard();

		$this->call('UserTableSeeder');
		$this->call('StationTableSeeder');
	}

}

class UserTableSeeder extends Seeder {
 
  public function run()
  {
  	User::truncate(); // deletes all existing users

  	$faker = Faker\Factory::create();
 
	for ($i = 0; $i < 100; $i++)
	{
	  $user = User::create(array(
	    'name' => $faker->userName,
	    'email' => $faker->email,
	    // 'password' => $faker->word,
	    'password' => Hash::make($faker->word),
	    'bonuspoints' => $faker->randomDigit,
	    'bonusdays' => $faker->randomDigit,
	    'visible' => $faker->boolean($chanceOfGettingTrue = 80)
	  ));
	}  
  }
 
}

class StationTableSeeder extends Seeder {
 
  public function run()
  {
  	Station::truncate(); // deletes all existing stations

  	$faker = Faker\Factory::create();
  	$client = new Client();

	$response = $client->get('http://opendata.brussel.be/api/records/1.0/search?dataset=villo-stations-beschikbaarheid-in-real-time&rows=1000&facet=bonus&facet=status&facet=contract_name&facet=banking');
	$json = $response->json();
	$stations = $json['records'];

	foreach ($stations as $station)
	{
	  Station::create(array(
	    'number' => $station['fields']['number'],
	    'name' => $station['fields']['name'],
	    'address' => $station['fields']['address'],
	    'longitude' => $station['geometry']['coordinates'][0],
	    'latitude' => $station['geometry']['coordinates'][1],
	    'banking' => $station['fields']['banking'],
	    'bonus' => $station['fields']['bonus'],
	    'status' => $station['fields']['status'],
	    'contract_name' => $station['fields']['contract_name'],
	    'bike_stands' => $station['fields']['bike_stands'],
	    'available_bike_stands' => $station['fields']['available_bike_stands'],
	   	'available_bikes' => $station['fields']['available_bikes'],
	    'last_update' => $station['fields']['last_update'],
	    'bonuspoints' => $faker->numberBetween($min = 0, $max = 10)
	  ));
	}  
  }
 
}