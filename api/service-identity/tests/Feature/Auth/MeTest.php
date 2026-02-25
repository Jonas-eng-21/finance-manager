<?php

namespace Tests\Feature\Auth;

use Illuminate\Foundation\Testing\RefreshDatabase;
use Tests\TestCase;
use App\Infrastructure\Persistence\Eloquent\UserModel;
use PHPOpenSourceSaver\JWTAuth\Facades\JWTAuth;

class MeTest extends TestCase
{
    use RefreshDatabase;

    public function test_it_should_return_401_if_unauthenticated(): void
    {
        $response = $this->getJson('/api/me');

        $response->assertStatus(401)
            ->assertJson(['message' => 'Unauthenticated.']);
    }

    public function test_it_should_return_user_data_if_authenticated(): void
    {
        // 1. Prepara o banco com um usuário real
        $userModel = UserModel::create([
            'name' => 'Jonas Sousa',
            'email' => 'jonas@example.com',
            'password' => password_hash('StrongPassword123!', PASSWORD_BCRYPT),
            'birth_date' => '1990-01-01',
        ]);

        $token = JWTAuth::fromUser($userModel);

        $response = $this->withHeaders([
            'Authorization' => 'Bearer ' . $token,
        ])->getJson('/api/me');

        $response->assertStatus(200)
            ->assertJsonFragment([
                'name' => 'Jonas Sousa',
                'email' => 'jonas@example.com',
            ])
            ->assertJsonMissing(['password']);
    }
}
