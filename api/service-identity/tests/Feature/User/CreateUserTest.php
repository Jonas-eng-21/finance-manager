<?php

namespace Tests\Feature\User;

use Illuminate\Foundation\Testing\RefreshDatabase;
use Tests\TestCase;

class CreateUserTest extends TestCase
{

    use RefreshDatabase;

    public function test_it_should_create_user_and_return_jwt_token(): void
    {
        $payload = [
            'name' => 'Jonas Sousa',
            'email' => 'jonas@example.com',
            'password' => 'StrongPassword123!',
            'birth_date' => '1990-01-01',
        ];


        $response = $this->postJson('/api/users', $payload);

        $response->assertStatus(201)
            ->assertJsonStructure(['token'])
            ->assertJsonMissing(['password']);

        $this->assertDatabaseHas('users', [
            'email' => 'jonas@example.com',
            'name' => 'Jonas Sousa',
        ]);
    }

    public function test_it_should_return_409_if_email_already_exists(): void
    {
        $payload = [
            'name' => 'Jonas Sousa',
            'email' => 'jonas@example.com',
            'password' => 'StrongPassword123!',
            'birth_date' => '1990-01-01',
        ];

        $this->postJson('/api/users', $payload);

        $response = $this->postJson('/api/users', $payload);

        $response->assertStatus(409)
            ->assertJsonFragment([
                'error' => __('identity.user.errors.email_already_exists')
            ]);
    }

    public function test_it_should_return_422_if_validation_fails(): void
    {
        $payload = [
            'name' => 'Jonas Sousa',
            'email' => 'email-invalido',
            'password' => '123',
        ];

        $response = $this->postJson('/api/users', $payload);

        $response->assertStatus(422)
            ->assertJsonValidationErrors(['email', 'password', 'birth_date']);
    }
}
