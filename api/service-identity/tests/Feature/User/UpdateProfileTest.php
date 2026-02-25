<?php

namespace Tests\Feature\User;

use Illuminate\Foundation\Testing\RefreshDatabase;
use Tests\TestCase;
use App\Infrastructure\Persistence\Eloquent\UserModel;
use PHPOpenSourceSaver\JWTAuth\Facades\JWTAuth;

class UpdateProfileTest extends TestCase
{
    use RefreshDatabase;

    private string $token;
    private UserModel $user;

    protected function setUp(): void
    {
        parent::setUp();

        $this->user = UserModel::create([
            'name' => 'Jonas Sousa',
            'email' => 'jonas@example.com',
            'password' => password_hash('StrongPassword123!', PASSWORD_BCRYPT),
            'birth_date' => '1990-01-01',
        ]);

        $this->token = JWTAuth::fromUser($this->user);
    }

    public function test_it_returns_401_if_unauthenticated(): void
    {
        $response = $this->putJson('/api/me', ['name' => 'Novo Nome']);
        $response->assertStatus(401);
    }

    public function test_it_updates_user_name_successfully(): void
    {
        $response = $this->withHeaders(['Authorization' => 'Bearer ' . $this->token])
            ->putJson('/api/me', ['name' => 'Jonas Atualizado']);

        $response->assertStatus(200)
            ->assertJson(['message' => 'Profile updated successfully']);

        $this->assertDatabaseHas('users', [
            'email' => 'jonas@example.com',
            'name' => 'Jonas Atualizado',
        ]);
    }

    public function test_it_returns_403_if_current_password_is_incorrect(): void
    {
        $payload = [
            'current_password' => 'WrongPass999!',
            'new_password' => 'NewStrongPass456!'
        ];

        $response = $this->withHeaders(['Authorization' => 'Bearer ' . $this->token])
            ->putJson('/api/me', $payload);

        $response->assertStatus(403)
        ->assertJsonFragment(['error' => __('identity.user.errors.invalid_current_password')]);
    }
}
