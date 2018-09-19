<template>
  <el-container>
    <el-header class="el-header-class">
      <div class="div-title">
        <span>RPC GOVERNANCE CONSOLE</span>
        <span style="float: right; font-size: 12px; margin-right: 10px">
          <a @click="logout" href="javascript:void(0)" style="color: white">退出</a>
        </span>
        <span style="float: right; font-size: 12px; margin-right: 10px">{{ currentUser }}</span>
      </div>
    </el-header>
    <el-main class="el-main-class">
      <div style="padding: 9px;background-color: #f3f5f5;border: #cccccc 1px solid" v-loading="loading">
        <div style="margin-bottom: 5px;">
          <el-input style="width: 350px;border-radius: 0px;" @keyup.enter.native="search" v-on:clear="search" v-model.trim="searchKey" size="small" placeholder="请输入服务名" clearable>
            <template slot="prepend">服务名</template>
          </el-input>
          <el-button type="primary" :loading="loading" icon="el-icon-search" size="mini"  style="border-radius: 0px;" @click="search">搜索</el-button>
        </div>
        <el-table
          :data="showServices.slice((currentPage-1)*pageSize,currentPage*pageSize)"
          style="width: 100%;">
          <el-table-column type="expand">
            <template slot-scope="props">
              <template v-for="(provider,index) in props.row.providers">
                <el-card shadow="hover" class="expand-card">
                  <el-row>
                    <el-col :span="16">
                      <label class="label">提供者:</label>
                      <div class="content">
                        <span>{{ provider.os }}</span>
                      </div>
                    </el-col>
                    <el-col :span="8">
                      <label class="label">加入时间:</label>
                      <div class="content">
                        <span>{{ provider.createTime|dateFormat }}</span>
                      </div>
                    </el-col>
                    <el-col :span="16">
                      <label class="label">地址:</label>
                      <div class="content">
                        <span>{{ provider.host }}</span>
                      </div>
                    </el-col>
                    <el-col :span="8">
                      <label class="label">端口:</label>
                      <div class="content">
                        <span>{{ provider.port }}</span>
                      </div>
                    </el-col>
                    <el-col :span="16">
                      <label class="label">权重:</label>
                      <div class="content">
                        <!--<span>{{ provider.weight }}</span>-->
                        <el-input-number v-model="provider.weight" size="mini" controls-position="right"
                                         @change="weightChange($event, props.row.name, provider)"
                                         :min="1" :max="200" :disabled="changeWeightLoading"></el-input-number>
                      </div>
                    </el-col>
                    <el-col :span="8">
                      <label class="label">状态:</label>
                      <div class="content">
                        <!--<span>{{ provider.active }}</span>-->
                        <el-switch
                          v-model="provider.active"
                          active-color="#36bf36"
                          inactive-color="#e60000"
                          active-text="上线"
                          inactive-text="下线"
                          @change="activeChange($event, props.row.name, provider)"
                          :disabled="changeActiveLoading">
                        </el-switch>
                      </div>
                    </el-col>
                  </el-row>
                </el-card>
              </template>
            </template>
          </el-table-column>
          <el-table-column
            prop="name"
            label="服务名">
          </el-table-column>
          <el-table-column
            prop="createTime"
            label="创建时间" sortable
            width="180" :formatter="formatDate">
          </el-table-column>
        </el-table>
        <div style="text-align: right;">
          <el-pagination
            small
            layout="prev, pager, next"
            :total="total"
            :page-size="pageSize"
            @current-change="current_change">
          </el-pagination>
        </div>
      </div>
    </el-main>
  </el-container>
</template>
<script>
  export default {
    name: 'index',
    data() {
      return {
        services: [],
        showServices: [],
        total: 0,
        pageSize: 10,
        currentPage: 1,
        changeWeightLoading: false,
        changeActiveLoading: false,
        currentUser: '',
        loading: false,
        searchKey: ''
      }
    },
    methods: {
      current_change:function(currentPage){
        this.currentPage = currentPage;
      },
      formatDate(row, column) {
        return this.dateFormat(row.createTime)
      },
      dateFormat(time) {
        let date = new Date(time);
        let Y = date.getFullYear() + '-';
        let M = date.getMonth() + 1 < 10 ? '0' + (date.getMonth() + 1) + '-' : date.getMonth() + 1 + '-';
        let D = date.getDate() < 10 ? '0' + date.getDate() + ' ' : date.getDate() + ' ';
        let h = date.getHours() < 10 ? '0' + date.getHours() + ':' : date.getHours() + ':';
        let m = date.getMinutes()  < 10 ? '0' + date.getMinutes() + ':' : date.getMinutes() + ':';
        let s = date.getSeconds() < 10 ? '0' + date.getSeconds() : date.getSeconds();
        return Y + M + D + h + m + s;
      },
      activeChange(active, serviceName, provider) {
        this.changeActiveLoading = true;
        let fd = new FormData();
        fd.append('serviceName',serviceName);
        fd.append('providerName',provider.os);
        fd.append('active',active);
        this.$axios.post(this.GLOBAL.httpUrlPrefix + "/changeActive",fd,{headers:{'Content-Type': 'application/x-www-form-urlencoded'}}).then((res) => {
          provider.weight = res.data.weight;
          provider.active = res.data.active;
          this.$message({
            message: '操作成功',
            type: 'success'
          });
          this.changeActiveLoading = false;
        }).catch((res) => {
          this.$message.error('操作失败');
          this.changeActiveLoading = false;
        });
      },
      weightChange(weight, serviceName, provider) {
        this.changeWeightLoading = true;
        let fd = new FormData();
        fd.append('serviceName',serviceName);
        fd.append('providerName',provider.os);
        fd.append('weight',weight);
        this.$axios.post(this.GLOBAL.httpUrlPrefix + "/changeWeight",fd,{headers:{'Content-Type': 'application/x-www-form-urlencoded'}}).then((res) => {
          provider.weight = res.data.weight;
          provider.active = res.data.active;
          this.$message({
            message: '操作成功',
            type: 'success'
          });
          this.changeWeightLoading = false;
        }).catch((res) => {
          this.$message.error('操作失败');
          this.changeWeightLoading = false;
        });
      },
      getServices(successCallbackFun, failCallbackFun) {
        this.$axios.get(this.GLOBAL.httpUrlPrefix + "/list").then((res) => {
          this.services = res.data;
          if (successCallbackFun) {
            successCallbackFun()
          }
        }).catch((res) => {
          if (failCallbackFun) {
            failCallbackFun()
          }
          if (res.response.status == 403) {
            this.$alert('重新登录', '没有权限', {
              confirmButtonText: '确定',
              callback: (action) => {
                this.$router.push({ name: 'login' });
              },
            });
          }
        });
      },
      logout() {
        this.$confirm('确定要退出?', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          this.$axios.get(this.GLOBAL.httpUrlPrefix + "/logout").then((res) => {
            this.$router.push({ name: 'login' });
          }).catch((res) => {
            console.error(res)
            this.$message.error('操作失败')
          });
        }).catch(() => {
        });
      },
      search() {
        this.loading = true;
        this.getServices(() => {
          if (this.searchKey != '') {
            let searchResult = [];
            let reg = new RegExp(this.searchKey);
            this.services.forEach(service => {
              if (service.name.match(reg)) {
                searchResult.push(service)
              }
            });
            this.showServices = searchResult;
          } else {
            this.showServices = this.services;
          }
          this.total = this.showServices.length;
          this.currentPage = 1;
          this.loading = false;
        }, () => {
          this.loading = false;
        })
      }
    },
    filters: {
      dateFormat(time) {
        let date = new Date(time);
        let Y = date.getFullYear() + '-';
        let M = date.getMonth() + 1 < 10 ? '0' + (date.getMonth() + 1) + '-' : date.getMonth() + 1 + '-';
        let D = date.getDate() < 10 ? '0' + date.getDate() + ' ' : date.getDate() + ' ';
        let h = date.getHours() < 10 ? '0' + date.getHours() + ':' : date.getHours() + ':';
        let m = date.getMinutes()  < 10 ? '0' + date.getMinutes() + ':' : date.getMinutes() + ':';
        let s = date.getSeconds() < 10 ? '0' + date.getSeconds() : date.getSeconds();
        return Y + M + D + h + m + s;
      }
    },
    created() {
      this.loading = true;
      this.$axios.get(this.GLOBAL.httpUrlPrefix + "/check", {

      }).then((res) => {
        if (res.data && res.data.success) {
          this.currentUser = res.data.username;
          if (this.currentUser != 'root') {
            this.changeWeightLoading = true;
            this.changeActiveLoading = true;
          }
          this.getServices(() => {
            this.showServices = this.services;
            this.total = this.showServices.length;
            this.loading = false;
          }, () => {
            this.loading = false;
          });
        } else {
          this.$router.push({ name: 'login' });
        }
      }).catch((res) => {
        console.error(res)
        this.$message.error('系统异常');
      });

    }
  }
</script>
<style>
  .expand-card{
    margin-top: 5px;
    border: 1px solid #EBEEF5;
    border-radius: 0;
    color: #909399;
  }
  .expand-card .label{
    display: inline-block;
    width: 80px;
    text-align: left;
    color: #606266;
  }
  .expand-card .content{
    display: inline-block;
  }
  .expand-card .el-col{
    margin-bottom: 8px;
  }
  .el-header-class {
    background-color: midnightblue;
  }
  .el-main-class {
    background-color: white;
  }
  .div-title {
    color: #f3f5f5;
    height: 60px;
    line-height: 60px;
  }
</style>
