<template>
  <el-row style="margin-top: 100px">
    <el-col :span="7">&nbsp;</el-col>
    <el-col :span="10">
      <el-card shadow="hover" class="expand-card">
        <el-form :model="ruleForm" status-icon :rules="rules" ref="ruleForm" label-width="100px"
                 class="demo-ruleForm" label-position="left">
          <el-form-item label="用户名:" prop="username">
            <el-input v-model="ruleForm.username"></el-input>
          </el-form-item>
          <el-form-item label="密码:" prop="password">
            <el-input type="password" v-model="ruleForm.password" autocomplete="off" @keyup.enter.native="submitForm('ruleForm')"></el-input>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="submitForm('ruleForm')">登录</el-button>
            <el-button @click="resetForm('ruleForm')">重置</el-button>
          </el-form-item>
        </el-form>
      </el-card>
    </el-col>
    <el-col :span="7">&nbsp;</el-col>
  </el-row>
</template>
<script>
  export default {
    data() {
      return {
        ruleForm: {
          password: '',
          username: ''
        },
        rules: {
          password: [
            {required : true, message: '请输入密码', trigger: 'blur' }
          ],
          username: [
            {required : true, message: '请输入用户名', trigger: 'blur' }
          ]
        }
      };
    },
    methods: {
      submitForm(formName) {
        this.$refs[formName].validate((valid) => {
          if (valid) {
            this.$axios.get(this.GLOBAL.httpUrlPrefix + "/login", {
              params: this.ruleForm
            }).then((res) => {
              if (res.data && res.data.success) {
                this.$router.push({ name: 'index' });
              } else {
                this.$message.error('账号或密码错误');
              }
            }).catch((res) => {
              console.error(res)
              this.$message.error('系统异常');
            });
          } else {
            console.log('error submit!!');
            return false;
          }
        });
      },
      resetForm(formName) {
        this.$refs[formName].resetFields();
      }
    }
  }
</script>
<style>
  .expand-card {
    width: 100%;
    border: 1px solid #EBEEF5;
    border-radius: 5px;
    color: #909399;
  }
</style>
