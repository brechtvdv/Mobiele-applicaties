<?php

use Illuminate\Database\Schema\Blueprint;
use Illuminate\Database\Migrations\Migration;

class CreateStationsTable extends Migration {

	/**
	 * Run the migrations.
	 *
	 * @return void
	 */
	public function up()
	{
		Schema::create('stations', function(Blueprint $table)
		{
			$table->increments('id');
			$table->integer('number')->unique;
			$table->string('name');
			$table->string('address');
			$table->double('longitude');
			$table->double('latitude');
			$table->boolean('banking');
			$table->boolean('bonus');
			$table->string('status');
			$table->string('contract_name');
			$table->integer('bike_stands');
			$table->integer('available_bike_stands');
			$table->integer('available_bikes');
			$table->string('last_update');
			$table->integer('bonuspoints');
			$table->timestamps();
		});
	}

	/**
	 * Reverse the migrations.
	 *
	 * @return void
	 */
	public function down()
	{
		Schema::drop('stations');
	}

}
