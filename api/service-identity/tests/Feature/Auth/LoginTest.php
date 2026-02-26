<?php

namespace Tests\Feature\Auth;

use Illuminate\Foundation\Testing\RefreshDatabase;
use Tests\TestCase;
use App\Infrastructure\Persistence\Eloquent\UserModel;

class LoginTest extends TestCase
{
    use RefreshDatabase;

    protected function setUp(): void
    {
        parent::setUp();

        UserModel::create([
            'name' => 'Jonas Sousa',
            'email' => 'jonas@example.com',
            'password' => password_hash('StrongPassword123!', PASSWORD_BCRYPT),
            'birth_date' => '1990-01-01',
        ]);
    }

    public function test_it_should_login_and_return_jwt_with_valid_credentials(): void
    {
        $payload = [
            'email' => 'jonas@example.com',
            'password' => 'StrongPassword123!',
        ];

        $response = $this->postJson('/api/login', $payload);

        $response->assertStatus(200)
            ->assertJsonStructure([
                'access_token',
                'refresh_token',
                'token_type',
                'expires_in'
            ]);
    }

    public function test_it_should_return_401_with_invalid_email(): void
    {
        $payload = [
            'email' => 'wrong@example.com',
            'password' => 'StrongPassword123!',
        ];

        $response = $this->postJson('/api/login', $payload);

        $response->assertStatus(401)
            ->assertJsonFragment([
                'error' => __('identity.auth.errors.invalid_credentials')
            ]);
    }

    public function test_it_should_return_401_with_invalid_password(): void
    {
        $payload = [
            'email' => 'jonas@example.com',
            'password' => 'WrongPassword999!',
        ];

        $response = $this->postJson('/api/login', $payload);

        $response->assertStatus(401)
            ->assertJsonFragment([
                'error' => __('identity.auth.errors.invalid_credentials')
            ]);
    }

    public function test_it_should_return_422_if_validation_fails(): void
    {
        $payload = [
            'password' => 'StrongPassword123!',
        ];

        $response = $this->postJson('/api/login', $payload);

        $response->assertStatus(422)
            ->assertJsonValidationErrors(['email']);
    }

    public function test_it_should_rate_limit_multiple_failed_login_attempts(): void
    {
        $payload = [
            'email' => 'hacker@example.com',
            'password' => 'wrongpassword'
        ];

        for ($i = 0; $i < 5; $i++) {
            $this->postJson('/api/login', $payload);
        }

        $response = $this->postJson('/api/login', $payload);

        $response->assertStatus(429);
    }
}
