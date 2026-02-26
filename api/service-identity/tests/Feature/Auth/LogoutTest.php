<?php

namespace Tests\Feature\Auth;

use App\Infrastructure\Persistence\Eloquent\UserModel;
use App\Infrastructure\Persistence\Eloquent\RefreshTokenModel;
use Illuminate\Foundation\Testing\RefreshDatabase;
use Illuminate\Support\Str;
use Tests\TestCase;

class LogoutTest extends TestCase
{
    use RefreshDatabase;

    private UserModel $user;
    private string $jwt;

    protected function setUp(): void
    {
        parent::setUp();

        $this->user = UserModel::create([
            'name' => 'Jonas Sousa',
            'email' => 'jonas@example.com',
            'password' => password_hash('StrongPassword123!', PASSWORD_BCRYPT),
            'birth_date' => '1990-01-01',
        ]);
    }

    public function test_it_should_logout_user_and_revoke_all_refresh_tokens(): void
    {
        $jwt = auth('api')->login($this->user);

        RefreshTokenModel::create([
            'id' => Str::uuid()->toString(),
            'user_id' => $this->user->id,
            'token_hash' => hash('sha256', 'token_telefone'),
            'expires_at' => now()->addDays(7),
        ]);

        RefreshTokenModel::create([
            'id' => Str::uuid()->toString(),
            'user_id' => $this->user->id,
            'token_hash' => hash('sha256', 'token_pc'),
            'expires_at' => now()->addDays(7),
        ]);

        $response = $this->withHeaders(['Authorization' => 'Bearer ' . $jwt])
            ->postJson('/api/logout');

        $response->assertStatus(200)
            ->assertJson(['message' => 'Successfully logged out']);

        $this->assertEquals(2, RefreshTokenModel::where('user_id', $this->user->id)->whereNotNull('revoked_at')->count());
    }

    public function test_it_returns_401_if_unauthenticated_user_tries_to_logout(): void
    {
        $response = $this->postJson('/api/logout');

        $response->assertStatus(401);
    }
}
